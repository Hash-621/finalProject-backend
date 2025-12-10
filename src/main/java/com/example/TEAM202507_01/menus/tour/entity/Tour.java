package com.example.TEAM202507_01.menus.tour.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TOUR") // DB 테이블 이름
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 관광지명
    private String address;     // 주소
    private String description; // 설명
    private String imageUrl;    // 이미지 URL

    // 필요한 필드가 있다면 더 추가하세요 (예: 운영시간, 전화번호 등)
}