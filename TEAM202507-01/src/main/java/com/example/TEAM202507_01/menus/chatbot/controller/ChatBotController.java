package com.example.TEAM202507_01.menus.chatbot.controller;

import com.example.TEAM202507_01.menus.chatbot.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final GeminiService geminiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        // 1. 사용자 질문 추출
        String userMessage = request.get("message");

        // 2. 서비스 호출 (★ 여기를 수정했습니다!)
        // getChatResponse -> getContents
        // (GeminiService에 정의된 메서드 이름과 똑같아야 합니다.)
        String aiResponse = geminiService.getContents(userMessage);

        // 3. 결과 반환
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}