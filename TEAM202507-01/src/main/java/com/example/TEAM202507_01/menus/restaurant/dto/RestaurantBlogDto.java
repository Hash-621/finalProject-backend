package com.example.TEAM202507_01.menus.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantBlogDto {
    // 네이버 API의 응답 껍데기
    private int total;
    private int start;
    private int display;
    private List<BlogItem> items;

    @Data
    @JsonPropertyOrder({ "title", "bloggername", "description", "postdate", "link", "thumbnail"})
    public static class BlogItem {
        private String title;
        private String link;        // 블로그 글 링크
        private String description;
        private String bloggername;
        private String postdate;

        // 🔥 우리가 크롤링해서 채워 넣을 필드
        private String thumbnail;
    }
}
