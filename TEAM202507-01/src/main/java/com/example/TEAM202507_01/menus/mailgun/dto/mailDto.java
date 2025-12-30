package com.example.TEAM202507_01.menus.mailgun.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
//@Getter: 롬복(Lombok) 라이브러리 기능.
// 클래스 내 모든 필드의 getXXX() 메서드(예: getTo())를 자동으로 생성해준다.
//데이터를 꺼낼 때 필요하다.

@Setter
//@Setter: 롬복 기능. setXXX() 메서드(예: setTo())를 자동으로 생성해준다.
// 데이터를 집어넣을 때 필요하다.
public class mailDto {
    // // private: 외부에서 직접 접근하지 못하게 막고, 메서드(Getter/Setter)를 통해서만 접근하게 하는 캡슐화 원칙을 지키기 위함이다
    //// String: 문자열 데이터를 저장하는 타입이다.

    private String to;    // 수신자 이메일 주소를 담는 변수이다.
    private String subject; // 메일 제목을 담는 변수이다.
    private String message; // 메일 본문 내용을 담는 변수이다.
}
