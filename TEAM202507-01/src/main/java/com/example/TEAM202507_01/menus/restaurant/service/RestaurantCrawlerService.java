package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantCrawlerService {

    private final RestaurantMapper restaurantMapper;
    private final String SAVE_PATH = "C:\\Users\\nextit\\Desktop\\RestaurantImages\\";

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