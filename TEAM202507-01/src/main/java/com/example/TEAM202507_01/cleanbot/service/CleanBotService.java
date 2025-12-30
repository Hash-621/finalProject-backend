package com.example.TEAM202507_01.cleanbot.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CleanBotService {

    @Value("${google.perspective.api-key}")
    private String apiKey;

    @Value("${google.perspective.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final double THRESHOLD = 0.30;

    public void checkContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        try {
            String requestUrl = apiUrl + "?key=" + apiKey;

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, String> comment = new HashMap<>();
            comment.put("text", text);
            requestBody.put("comment", comment);
            requestBody.put("languages", Collections.singletonList("ko"));

            Map<String, Object> requestedAttributes = new HashMap<>();
            requestedAttributes.put("TOXICITY", new HashMap<>());
            requestBody.put("requestedAttributes", requestedAttributes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<PerspectiveResponse> response = restTemplate.postForEntity(requestUrl, entity, PerspectiveResponse.class);

            if (response.getBody() != null && response.getBody().getAttributeScores() != null) {
                double score = response.getBody()
                        .getAttributeScores()
                        .get("TOXICITY")
                        .getSummaryScore()
                        .getValue();

                log.info("🤖 [CleanBot] 분석 결과: '{}', 점수: {}", text, score);

                if (score > THRESHOLD) {
                    // ★ 진짜 욕설일 때만 예외 발생 (이건 막아야 함)
                    throw new RuntimeException("🚫 부적절한 표현이 감지되었습니다.");
                }
            }
        } catch (RuntimeException e) {
            // 우리가 발생시킨 "부적절한 표현" 예외는 그대로 던져서 막아야 함
            if (e.getMessage() != null && e.getMessage().contains("부적절한 표현")) {
                throw e;
            }
            // ★ 그 외의 에러(API 키 오류, 403 등)는 로그만 찍고 통과시킴!
            log.error("⚠️ 클린봇 시스템 오류 (댓글 등록 허용): {}", e.getMessage());
        } catch (Exception e) {
            // ★ 나머지 모든 에러도 통과시킴
            log.error("⚠️ 클린봇 알 수 없는 오류 (댓글 등록 허용): {}", e.getMessage());
        }
    }

    @Data
    private static class PerspectiveResponse {
        private Map<String, AttributeScore> attributeScores;
    }

    @Data
    private static class AttributeScore {
        private SummaryScore summaryScore;
    }

    @Data
    private static class SummaryScore {
        private double value;
    }
}