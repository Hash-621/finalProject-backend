package com.example.TEAM202507_01.menus.restaurant.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RestaurantReviewDto {
    private Long id; // 리뷰 ID review_id
    private Long restaurantId; // 식당 ID rest_id
    private String userId; // 작성자 ID user_id
    private String content; // 리뷰 내용 content_id
    private double rating; // 평점 rating
    private LocalDate createdAt; // 작성일시 created_at
}