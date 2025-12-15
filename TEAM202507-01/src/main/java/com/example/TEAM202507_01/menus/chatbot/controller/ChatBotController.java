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
        String userMessage = request.get("message");
        String aiResponse = geminiService.getChatResponse(userMessage);
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}