package com.example.TEAM202507_01.menus.mailgun.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class mailDto {
    private String to;      // 수신자
    private String subject; // 제목
    private String message; // 본문 내용
}
