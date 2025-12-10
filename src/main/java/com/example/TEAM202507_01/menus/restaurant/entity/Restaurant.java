package com.example.TEAM202507_01.menus.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RESTAURANT") // DB 테이블 이름
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 식당 이름
    private String category;    // 음식 종류 (한식, 중식 등)
    private String address;     // 주소
    private String phone;       // 전화번호
    private String description; // 설명

    // 필요한 필드들을 추가하세요 (운영시간, 메뉴 등)
}