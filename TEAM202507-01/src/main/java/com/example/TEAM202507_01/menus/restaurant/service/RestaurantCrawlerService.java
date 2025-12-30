package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantCrawlerService {

    private final RestaurantMapper restaurantMapper;
    // 이미지를 저장할 로컬 경로 (윈도우 바탕화면 경로로 설정되어 있음)
    private final String SAVE_PATH = "C:\\Users\\nextit\\Desktop\\RestaurantImages\\";
    private final RestTemplate restTemplate = new RestTemplate();

    // JSON 파싱 도구. 모르는 필드가 있어도 에러 내지 말고 무시하라는 설정 추가.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // [기능 1] 대전시 오픈 API에서 식당 데이터 가져오기 (동기화)
    @Transactional
    public String syncRestaurantData() {
        System.out.println("========== [동기화 시작] ==========");
        int totalSuccess = 0;

        // 🔥 [핵심 1] 브라우저인 척 속이기 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        // 브라우저인 척 속이기 위한 User-Agent 설정 (봇 차단 방지)
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 1페이지부터 10페이지까지 반복 요청
        for (int i = 1; i <= 10; i++) {
            String url = "https://bigdata.daejeon.go.kr/api/stores/?page=" + i;
            System.out.println("\n>> " + i + "페이지 요청 중: " + url);

            try {
                // API 호출 (exchange 메서드 사용)
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        URI.create(url), HttpMethod.GET, entity, String.class);

                String jsonString = responseEntity.getBody();

                // 🔍 [디버깅] 진짜 데이터가 왔는지 눈으로 확인
                if (jsonString == null || jsonString.isEmpty()) {
                    System.out.println("🚨 " + i + "페이지 응답이 비어있음 (NULL/Empty)");
                    continue;
                }
                // 앞부분 300자만 찍어서 확인
                System.out.println("🔍 응답 내용(앞부분): " + jsonString.substring(0, Math.min(jsonString.length(), 300)));

                // 2. 파싱
                ResponseWrapper response = objectMapper.readValue(jsonString, ResponseWrapper.class);

                if (response == null) {
                    System.out.println("🚨 파싱 실패: response 객체가 NULL");
                    continue;
                }
                if (response.getResults() == null) {
                    System.out.println("🚨 파싱 실패: results 리스트가 NULL (JSON 키 불일치 의심)");
                    continue;
                }
                if (response.getResults().isEmpty()) {
                    System.out.println("⚠️ 파싱 성공했으나 데이터가 0건입니다.");
                    continue;
                }

                System.out.println("✅ 파싱 성공! 데이터 개수: " + response.getResults().size());

                // 가져온 식당 리스트를 DB에 저장
                for (RestaurantDto dto : response.getResults()) {
//                    dto.setCategory("RESTAURANT");

                    try {
                        // 리스트 필드가 null이면 빈 리스트로 초기화 (NullPointerException 방지)
                        if (dto.getMenu() == null) dto.setMenu(new ArrayList<>());
                        if (dto.getPrice() == null) dto.setPrice(new ArrayList<>());
                        if (dto.getMenuDetail() == null) dto.setMenuDetail(new ArrayList<>());

                        // DB 저장
                        restaurantMapper.save(dto);
                        totalSuccess++;

                        // 첫 번째 데이터만 저장 성공 로그 찍기 (너무 많으니까)
                        if (totalSuccess % 10 == 0) System.out.print(".");

                    } catch (Exception e) {
                        // 에러 나도 멈추지 않고 로그만 찍고 다음 식당 처리
                        System.err.println("\n❌ 저장 에러 (ID: " + dto.getName() + "): " + e.getMessage());
                        // e.printStackTrace(); // 필요하면 주석 해제
                    }
                }

            } catch (Exception e) {
                System.err.println("\n💥 API 호출 중 에러: " + e.getMessage());
                // 페이지 요청 실패 시 로그 찍고 다음 페이지로
                e.printStackTrace();
            }
        }

        String resultMsg = "\n========== [동기화 종료] 총 " + totalSuccess + "건 저장됨 ==========";
        System.out.println(resultMsg);

        return resultMsg;
    }

    // 내부 클래스 (static 필수)
    @Data
    public static class ResponseWrapper {
        private int count;
        private String next;

        @JsonAlias("results") // JSON의 "results" 키와 매핑
        private List<RestaurantDto> results;
    }




    @Async
    public void crawlStoreImages() {
        System.out.println("=== 🕷️ 안전 모드 크롤링 시작 (1건씩 처리) ===");

        // 1. 저장 폴더 생성
        File folder = new File(SAVE_PATH);
        if (!folder.exists()) folder.mkdirs();

        // 2. 드라이버 매니저 설정 (최초 1회만)
        WebDriverManager.chromedriver().setup();

        try {
            List<RestaurantDto> storeList = restaurantMapper.findAllWithUrl();

            int count = 0;
            for (RestaurantDto store : storeList) {
                // URL 없으면 패스
                if (store.getUrl() == null || store.getUrl().isEmpty()) continue;

                // (선택) 이미지가 이미 있으면 패스 (중단 후 재시작 시 유용)
                // if (store.getImagePath() != null) continue;

                System.out.println("\n>> [" + (count + 1) + "/" + storeList.size() + "] 처리 중: " + store.getName());

                // 🔥 [핵심] 루프 안에서 브라우저를 켜고 끕니다. (세션 오류 원천 차단)
                WebDriver driver = null;
                try {
                    // 크롬 옵션 설정
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--remote-allow-origins=*");
                    options.addArguments("--start-maximized");
                    options.addArguments("--disable-popup-blocking");
                    options.addArguments("--headless"); // 🔥 화면 안 띄우고 백그라운드 실행 (속도 향상)
                    // 화면을 보고 싶으면 위 "--headless" 줄을 주석 처리하세요.

                    driver = new ChromeDriver(options);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                    driver.get(store.getUrl());
                    Thread.sleep(1500); // 로딩 대기

                    // --- 프레임 진입 ---
                    try {
                        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("entryIframe"));
                    } catch (Exception e) {
                        // 프레임 없으면 패스
                    }

                    // --- 이미지 찾기 (role='main' -> a -> img) ---
                    List<WebElement> images = driver.findElements(By.cssSelector("div[role='main'] a img"));
                    String targetImgSrc = null;

                    if (images.isEmpty()) {
                        // 예비책
                        images = driver.findElements(By.cssSelector("div[role='main'] img"));
                    }

                    for (WebElement img : images) {
                        String src = img.getAttribute("src");
                        // 유효한 이미지인지 검사
                        if (src != null && src.startsWith("http") && !src.contains("data:image") && !src.contains(".svg")) {
                            targetImgSrc = src;
                            break;
                        }
                    }

                    // --- 다운로드 및 저장 ---
                    if (targetImgSrc != null) {
                        String fileName = store.getId() + ".jpg";
                        downloadImage(targetImgSrc, fileName);
                        System.out.println("DEBUG: ID=" + store.getId() + ", FILE=" + fileName); // 이 로그 확인

                        restaurantMapper.updateImage(store.getId(), fileName);
                        System.out.println("   ✅ 저장 완료!");
                        count++;
                    } else {
                        System.out.println("   ⚠️ 이미지 못 찾음");
                    }

                } catch (Exception e) {
                    System.err.println("   💥 " + store.getName() + " 처리 중 에러: " + e.getMessage());
                    // 에러가 나도 다음 가게로 넘어갑니다 (멈추지 않음)
                } finally {
                    // 🔥 [필수] 다 썼으면 즉시 브라우저 끄기
                    if (driver != null) {
                        try { driver.quit(); } catch (Exception e) {}
                    }
                }

                // 너무 빠르면 네이버가 차단하므로 1초 휴식
                Thread.sleep(1000);
            }

            System.out.println("=== 🎉 전체 크롤링 종료 (성공: " + count + "건) ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 파일 다운로드 메서드
    private void downloadImage(String imageUrl, String fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(SAVE_PATH + fileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            System.err.println("   ❌ 다운로드 실패: " + e.getMessage());
        }
    }
}