package com.example.TEAM202507_01.menus.community.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CommentFilterService {

    // application.properties에 google.api-key가 있어야 합니다.
    @Value("${google.api-key}")
    private String apiKey;

    private final String API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";

    public boolean isToxic(String commentText) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("⚠️ Google API Key가 설정되지 않았습니다. 필터링을 건너뜁니다.");
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + apiKey;

        // 요청 JSON 바디 구성
        Map<String, Object> request = new HashMap<>();

        Map<String, String> comment = new HashMap<>();
        comment.put("text", commentText);
        request.put("comment", comment);

        Map<String, Object> requestedAttributes = new HashMap<>();
        requestedAttributes.put("TOXICITY", new HashMap<>());
        request.put("requestedAttributes", requestedAttributes);

        try {
            // 구글 API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // 응답 파싱
            Map<String, Object> body = response.getBody();
            if (body != null) {
                Map<String, Object> attributeScores = (Map<String, Object>) body.get("attributeScores");
                Map<String, Object> toxicity = (Map<String, Object>) attributeScores.get("TOXICITY");
                Map<String, Object> summaryScore = (Map<String, Object>) toxicity.get("summaryScore");
                Double score = (Double) summaryScore.get("value");

                log.info("🤖 댓글 욕설 확률: {} ({})", score, commentText);

                // 0.7 (70%) 이상이면 욕설로 판단하여 true 반환
                return score > 0.7;
            }
        } catch (Exception e) {
            log.error("🔥 필터링 API 호출 중 오류 발생", e);
        }

        // 오류 발생 시 글쓰기를 막지 않기 위해 false 반환 (보수적 접근 시 true)
        return false;
    }
}