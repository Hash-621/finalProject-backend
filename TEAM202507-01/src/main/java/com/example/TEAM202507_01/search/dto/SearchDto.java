package com.example.TEAM202507_01.search.dto;

import com.example.TEAM202507_01.search.document.*;
// 나중에 추가될 ReviewDocument 등 import...
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchDto {
    // 1. 식당 검색 결과
    private List<RestaurantDocument> restaurants;

    private List<TourDocument> tours;

    private List<TourPostDocument> tourPosts;

    private List<NewsDocument> news;

    private List<JobDocument> jobs;

    private List<JobUserPostDocument> jobPosts;

    private List<HospitalDocument> hospitals;

    private List<CommunityPostDocument> communityPosts;
}