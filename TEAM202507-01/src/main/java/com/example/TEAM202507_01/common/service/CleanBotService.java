package com.example.TEAM202507_01.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CleanBotService {

    @Value("${google.perspective.key}")
    private String apiKey;
    @Value("${google.perspective.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void checkContent(String text) {
        if (text == null || text.trim().isEmpty()) return;

        try {
            String requestUrl = apiUrl + "?key=" + apiKey;
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("comment", Map.of("text", text));
            requestBody.put("languages", new String[]{"ko"});
            requestBody.put("requestedAttributes", Map.of("TOXICITY", new HashMap<>()));

            ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, requestBody, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                Map<String, Object> attr = (Map) body.get("attributeScores");
                Map<String, Object> toxicity = (Map) attr.get("TOXICITY");
                Map<String, Object> summary = (Map) toxicity.get("summaryScore");
                Double score = (Double) summary.get("value");

                // 유해 확률 70% 이상이면 차단
                if (score > 0.7) {
                    throw new RuntimeException("게시글에 부적절한 표현(욕설 등)이 포함되어 등록할 수 없습니다.");
                }
            }
        } catch (RuntimeException e) {
            throw e; // 우리가 발생시킨 에러는 그대로 전달
        } catch (Exception e) {
            System.out.println("클린봇 API 오류(통과 처리): " + e.getMessage());
        }
    }
}