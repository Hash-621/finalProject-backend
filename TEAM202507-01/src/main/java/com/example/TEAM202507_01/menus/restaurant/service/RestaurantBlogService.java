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

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    // [수정 1] 리턴 타입을 List -> RestaurantBlogDto로 변경
    public RestaurantBlogDto searchBlogList(Long restaurantId) {

        // 1. DB에서 식당 이름 조회
        RestaurantDto restaurant = restaurantMapper.findNameById(restaurantId);
        if (restaurant == null) {
            // 빈 DTO 반환 (null 리턴보다 안전)
            return new RestaurantBlogDto();
        }

        // 2. 검색어 만들기
        String query = "대전 " + restaurant.getName();

        // 3. URI 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/blog.json")
                .queryParam("query", query)
                .queryParam("display", 100) // 5개 정도만
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUri();

        // 4. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(headers).build();

        try {
            // 5. 요청 및 응답
            ResponseEntity<RestaurantBlogDto> response = restTemplate.exchange(requestEntity, RestaurantBlogDto.class);

            // [수정 2] Body 전체(DTO)를 가져옴
            RestaurantBlogDto resultDto = response.getBody();

            // 응답이 비어있으면 빈 객체 반환
            if (resultDto == null || resultDto.getItems() == null) {
                return new RestaurantBlogDto();
            }

            // 6. 썸네일 크롤링 (items 리스트에 대해 수행)
            // parallelStream 사용 추천 (속도 향상)
            resultDto.getItems().parallelStream().forEach(item -> {
                try {
                    String thumb = crawlOgImage(item.getLink());
                    item.setThumbnail(thumb);

                    // HTML 태그 제거 (뉴스 코드 참조)
                    item.setTitle(cleanHtml(item.getTitle()));
                    item.setDescription(cleanHtml(item.getDescription()));
                } catch (Exception e) {
                    // 크롤링 실패해도 무시하고 진행
                }
            });

            // [수정 3] 리스트가 아닌 DTO 전체를 반환!
            // 이렇게 해야 프론트에서 response.data.items 로 접근 가능함
            return resultDto;

        } catch (Exception e) {
            e.printStackTrace();
            return new RestaurantBlogDto();
        }
    }

    private String crawlOgImage(String blogLink) {
        if (blogLink == null || !blogLink.contains("blog.naver.com")) {
            return null;
        }
        // 모바일 주소 변환
        String mobileUrl = blogLink.replace("https://blog.naver.com", "https://m.blog.naver.com");

        try {
            Document doc = Jsoup.connect(mobileUrl)
                    .timeout(2000)
                    .userAgent("Mozilla/5.0")
                    .get();
            Element metaOgImage = doc.selectFirst("meta[property=og:image]");
            if (metaOgImage != null) {
                return metaOgImage.attr("content");
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    // HTML 태그 제거 유틸 메서드
    private String cleanHtml(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&nbsp;", " ");
    }
}