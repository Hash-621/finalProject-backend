package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantBlogDto;
import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantBlogService {

    private final RestaurantMapper restaurantMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    // application.properties 파일에 있는 네이버 API 키값을 가져옴
    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public List<RestaurantBlogDto.BlogItem> searchBlogList(Long restaurantId) {

        // 1. DB에서 식당 이름 조회
        RestaurantDto restaurant = restaurantMapper.findNameById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("해당 ID의 식당이 존재하지 않습니다: " + restaurantId);
        }

        // 2. 검색어 만들기: "대전 맛집" + "식당이름" (정확도를 높이기 위함)
        String query = "대전 맛집 " + restaurant.getName();
        System.out.println(">> 블로그 검색어: " + query);

        // 3. 네이버 API 요청 주소(URI) 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/blog.json")
                .queryParam("query", query) // 검색어
                .queryParam("display", 10) // 10개만
                .queryParam("start", 1)
                .queryParam("sort", "sim") // 관련도순 정렬
                .encode()
                .build()
                .toUri();

        // 4. 헤더에 ID와 Secret 넣기 (네이버 API 필수 요구사항)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(headers).build();

        // 5. 요청 보내고 응답 받기
        try {
            ResponseEntity<RestaurantBlogDto> response = restTemplate.exchange(requestEntity, RestaurantBlogDto.class);
            List<RestaurantBlogDto.BlogItem> items = response.getBody().getItems();
            // 6. [병렬 처리] 블로그 글마다 들어가서 썸네일 이미지 긁어오기
            // parallelStream(): 여러 스레드를 동시에 써서 속도를 높임.
            if (items == null || items.isEmpty()) {
                return Collections.emptyList();
            }

            // 6. 🔥 [병렬 처리] Jsoup으로 썸네일 크롤링
            // stream() 대신 parallelStream()을 써야 동시에 여러군데 접속해서 빠릅니다.
            items.parallelStream().forEach(item -> {
                String thumb = crawlOgImage(item.getLink());
                item.setThumbnail(thumb); // DTO에 썸네일 주입

                // 제목에 <b>태그 같은 게 섞여와서 제거함 (정규식 사용)
                item.setTitle(item.getTitle().replaceAll("<[^>]*>", ""));
                item.setDescription(item.getDescription().replaceAll("<[^>]*>", ""));
            });

            return items;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // 에러 나면 빈 리스트 반환
        }
    }

    // Jsoup을 이용한 가벼운 크롤링 (HTML 파싱)
    private String crawlOgImage(String blogLink) {
        // ... (네이버 블로그만 처리)
        // 모바일 주소로 변환 (데이터량이 적어서 빠름)
        if (!blogLink.contains("blog.naver.com")) {
            return null; // 네이버 블로그 아니면 패스
        }

        // PC 링크 -> 모바일 링크 변환 (속도 및 파싱 용이성)
        String mobileUrl = blogLink.replace("https://blog.naver.com", "https://m.blog.naver.com");

        try {
            // Jsoup으로 해당 URL 접속해서 HTML 가져옴
            Document doc = Jsoup.connect(mobileUrl)
                    .timeout(2000) // 2초 안에 안 되면 포기 (속도 중요)
                    .userAgent("Mozilla/5.0") // 봇 차단 방지
                    .get();
            // 메타 태그 중 og:image (대표 이미지) 값을 찾아서 반환
            Element metaOgImage = doc.selectFirst("meta[property=og:image]");
            if (metaOgImage != null) {
                return metaOgImage.attr("content");
            }
        } catch (Exception e) {
            // 크롤링 실패는 조용히 넘김 (이미지 없어도 글은 보여야 하니까)
            // System.err.println("썸네일 추출 실패: " + mobileUrl);
        }
        return null;
    }
}