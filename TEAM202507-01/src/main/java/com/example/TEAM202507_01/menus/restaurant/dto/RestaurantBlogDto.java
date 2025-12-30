package com.example.TEAM202507_01.menus.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.util.List;

@Data
public class RestaurantBlogDto {
    // 네이버 API가 주는 전체 결과 수
    private int total;
    // 시작 위치
    private int start;
    // 한 번에 보여줄 개수
    private int display;
    // 실제 블로그 글 목록 (아래 내부 클래스 BlogItem의 리스트)
    private List<BlogItem> items;

    // 내부 클래스 (Inner Class): 블로그 글 하나하나의 정보를 담음.
    // static으로 선언해야 외부에서 이 클래스만 따로 쓸 때 문제가 없음.
    @Data
    @JsonPropertyOrder({ "title", "bloggername", "description", "postdate", "link", "thumbnail"})
    public static class BlogItem {
        private String title;       // 글 제목
        private String link;        // 글 링크
        private String description; // 글 요약
        private String bloggername; // 블로거 이름
        private String postdate;    // 작성일

        // 이 필드는 네이버 API가 주는 게 아니라, 우리가 크롤링해서 추가로 채워 넣을 썸네일 주소임.
        private String thumbnail;
    }
}
