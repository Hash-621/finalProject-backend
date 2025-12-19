package com.example.TEAM202507_01.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanBotService {

    @Value("${google.perspective.key}")
    private String apiKey;

    @Value("${google.perspective.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    //    // 🛑 차단 기준 점수 (0.7 = 70% 이상 유해하면 차단)
    private static final double THRESHOLD = 0.20;

    public void checkContent(String text) {
        if (text == null || text.trim().isEmpty()) return;

        try {
            String requestUrl = apiUrl + "?key=" + apiKey;

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("comment", Map.of("text", text));
            requestBody.put("languages", new String[]{"ko"}); // 한국어
            requestBody.put("requestedAttributes", Map.of("TOXICITY", new HashMap<>()));

            // API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, requestBody, Map.class);

            // 결과 파싱
            Map<String, Object> body = response.getBody();
            if (body != null) {
                Map<String, Object> attr = (Map) body.get("attributeScores");
                Map<String, Object> toxicity = (Map) attr.get("TOXICITY");
                Map<String, Object> summary = (Map) toxicity.get("summaryScore");
                Double score = (Double) summary.get("value");

                log.info("🤖 [CleanBot] '{}' 유해 점수: {}", text, score);

                if (score > THRESHOLD) {
                    throw new RuntimeException("🚫 부적절한 표현이 감지되어 등록이 차단되었습니다.");
                }
            }
        } catch (RuntimeException e) {
            throw e; // 욕설 감지 에러는 그대로 던짐
        } catch (Exception e) {
            log.error("🔥 클린봇 API 오류 (일단 통과시킴): {}", e.getMessage());
        }
    }
}