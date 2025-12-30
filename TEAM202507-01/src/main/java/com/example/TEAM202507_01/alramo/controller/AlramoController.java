package com.example.TEAM202507_01.alramo.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//의미: 이 클래스가 Restful 웹 서비스의 컨트롤러임을 선언

@RequestMapping("api/v1")
//의미: 이 컨트롤러로 들어오는 기본 주소(URL)를 설정

@AllArgsConstructor
//의미: 롬복(Lombok) 라이브러리 기능, 모든 필드(변수)를 초기화하는 생성자를 자동으로 만들어 줌

public class AlramoController {
    private final AlramoService alramoService;
    // AlramoService 타입의 변수 alramoService를 선언
    //private: 이 클래스 내부에서만 쓰겠다 (외부 접근 금지).
    // final: 한 번 정해지면 절대 바뀌지 않는다는 뜻. 필수적으로 값이 채워져야 함을 의미

    @GetMapping("/test")
    //의미: HTTP 요청 중 GET 방식으로 들어오는 요청을 처리
    //private: 이 클래스 내부에서만 쓰겠다 (외부 접근 금지).

    public ResponseEntity<String> test() {
//        alramoService.sendNewPostNotification();

        return ResponseEntity.ok("This is a test");
        //해석: ok라는 상태(HTTP 200 성공)와 함께 "This is a test"라는 글자를 담아서 사용자에게 돌려줌

    }
}
