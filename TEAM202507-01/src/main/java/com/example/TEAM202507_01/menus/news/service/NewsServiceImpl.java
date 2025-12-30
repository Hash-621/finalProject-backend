package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.repository.NewsMapper;
import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
// [어노테이션 설명]
// @Service: 스프링에게 "이 클래스는 비즈니스 로직을 담당하는 서비스야"라고 알려줌 (Bean 등록).
// @RequiredArgsConstructor: final이 붙은 필드에 대한 생성자를 자동으로 생성해줌 (의존성 주입 용도).
// @Transactional: 이 클래스의 모든 메서드에 트랜잭션을 적용함.
//메서드 실행 중 에러가 나면 자동으로 롤백(취소)하여 데이터 무결성을 지킴.
@Service
@RequiredArgsConstructor
@Transactional
@PropertySource(value = "classpath:application.properties")
public class NewsServiceImpl implements NewsService {

    // 의존성 주입: DB 작업을 위해 Mapper를 가져옴.
    private final NewsMapper newsMapper;

    // 의존성 주입: 검색 기능(엘라스틱서치 등)을 위해 SearchService를 가져옴.
    private final SearchService searchService; // ★ 검색 서비스 주입

    private final RestTemplate restTemplate;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션. 성능 최적화(변경 감지 안 함)를 위해 사용.
    public List<NewsDto> findAll() {
        return newsMapper.findAll();
    } // 매퍼에게 전체 목록을 달라고 요청함.

    // [상세 조회 구현]
    @Override
    @Transactional(readOnly = true) // 읽기 전용.
    public NewsDto findById(Long id) {
        NewsDto news = newsMapper.findById(id); // 매퍼에게 ID로 찾으라고 시킴.
        if (news == null) { // 만약 DB에 없으면
            // 예외를 발생시켜서 "못 찾았다"고 알림.
            throw new RuntimeException("해당 뉴스를 찾을 수 없습니다. ID: " + id);
        }
        return news; // 찾은 뉴스 반환.
    }
    // [저장/수정 구현]
//    @Override
//    public void save(NewsDto news) {
//        // 1. DB 저장
//        if (news.getId() == null) {
//            // ID가 없으면 '새 글'로 간주하고 매퍼의 insert(save) 호출.
//            newsMapper.save(news);
//        } else {
//            newsMapper.update(news);
//        }
//    }
    // (참고) 여기서 searchService 등을 호출해 검색엔진에도 저장하는 코드가 추가될 수 있음.

    // [삭제 구현]
    @Override
    public void delete(Long id) {
        newsMapper.delete(id);  // 매퍼에게 삭제하라고 시킴.
        // (선택) 삭제 시 검색엔진 데이터 삭제 로직 추가 가능
    }

    public Object getDaejeonNews(String query, int display, int start) {

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.json")
                .queryParam("query", "대전 " + query)
                .queryParam("display", display) // 4
                .queryParam("start", start)     // 계산된 시작 값 (1, 5, 9...)
                .queryParam("sort", "sim")      // (선택) 정확도순 or 날짜순(date)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<NewsDto> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, NewsDto.class
        );

        NewsDto dto = response.getBody();

        // [중요] 뉴스 리스트를 돌면서 이미지 주소를 하나씩 채워넣습니다.
        if (dto != null && dto.getItems() != null) {
            for (NewsDto.NewsItem item : dto.getItems()) {
                String thumbnail = scrapeThumbnail(item.getLink());
                item.setThumbnail(thumbnail); // DTO에 thumbnail 필드가 있어야 합니다!
            }
        }

        return dto;
    }

    // 뉴스 기사 URL에서 대표 이미지(Open Graph)를 추출하는 함수
    private String scrapeThumbnail(String url) {
        try {
            // 뉴스 사이트에 접속 (Timeout을 짧게 설정해야 전체 응답이 안 느려집니다)
            Document doc = Jsoup.connect(url)
                    .timeout(2000) // 2초 안에 응답 안 오면 포기
                    .userAgent("Mozilla/5.0") // 봇이 아닌 척 하기
                    .get();

            // <meta property="og:image" content="..."> 태그를 찾음
            String imageUrl = doc.select("meta[property=og:image]").attr("content");

            return imageUrl.isEmpty() ? null : imageUrl;
        } catch (Exception e) {
            // 에러 나면 그냥 이미지 없는 상태로 둠
            return null;
        }
    }
}