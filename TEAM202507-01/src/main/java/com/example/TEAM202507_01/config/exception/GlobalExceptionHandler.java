package com.example.TEAM202507_01.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 에러를 다 잡아서 콘솔에 출력
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllException(Exception e) {
        log.error("🔥🔥🔥 [서버 에러 발생] 🔥🔥🔥", e); // 콘솔에 빨간색 에러 로그 출력
        e.printStackTrace(); // 상세 에러 내용 출력

        Map<String, String> response = new HashMap<>();
        response.put("error", "서버 내부 오류 발생");
        response.put("message", e.getMessage()); // 에러 메시지를 프론트엔드로 보냄

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}