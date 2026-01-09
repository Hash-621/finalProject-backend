package com.example.TEAM202507_01.menus.chatbot.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import com.example.TEAM202507_01.menus.tour.repository.TourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    // ★ DB 데이터 조회를 위한 Mapper 주입
    private final RestaurantMapper restaurantMapper;
    private final TourMapper tourMapper;

    public String getContents(String prompt) {
        try {
            String requestUrl = apiUrl + "?key=" + apiKey;

            // 1. DB에서 데이터 가져오기 (전체를 다 가져오면 토큰이 너무 많아지므로, 추천/인기 리스트 일부만 가져오는 것 권장)
            // 여기서는 예시로 전체를 가져온다고 가정하거나, 로직에 따라 상위 5~10개만 끊어서 가져오세요.
            List<RestaurantDto> restaurants = restaurantMapper.findAll();
            List<TourDto> tours = tourMapper.findAll();

            // 2. 데이터를 문자열(Context)로 변환
            String dataContext = buildDataContext(restaurants, tours);

            // 3. 시스템 프롬프트 구성 (AI에게 역할과 데이터, 출력 형식을 알려줌)
            String systemInstruction = """
                    너는 '방방곡곡' 사이트의 AI 가이드야.
                    아래 제공된 [우리 사이트 데이터]를 기반으로 사용자에게 추천해줘.
                    
                    [답변 규칙]
                    1. 반드시 아래 제공된 데이터 내에서만 추천해. 데이터에 없으면 없다고 해.
                    2. 출력 형식은 가독성 있게 마크다운(Markdown)을 사용해. (볼드체, 리스트 등)
                    3. 추천하는 장소의 이름에는 반드시 상세 페이지로 이동하는 링크를 걸어줘.
                       - 맛집 링크 형식: [가게이름](/restaurant/가게ID)
                       - 관광지 링크 형식: [관광지이름](/tour/attraction?keyword=관광지이름)
                    4. 이모지(🍱, 🏞️ 등)를 적절히 섞어서 친근하게 답변해.
                    
                    [우리 사이트 데이터]
                    %s
                    """.formatted(dataContext);

            // 4. 최종 프롬프트 조합 (시스템 지시 + 사용자 질문)
            String finalPrompt = systemInstruction + "\n\n사용자 질문: " + prompt;

            // --- 기존 API 호출 로직 ---
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> parts = new HashMap<>();

            parts.put("text", finalPrompt); // 수정된 프롬프트 사용
            content.put("parts", new Map[]{parts});
            contents.add(content);
            requestBody.put("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
                    if (!partsResponse.isEmpty()) {
                        return (String) partsResponse.get(0).get("text");
                    }
                }
            }
            return "죄송합니다. 답변을 생성할 수 없습니다.";

        } catch (Exception e) {
            log.error("Gemini API Error", e);
            return "오류가 발생했습니다: " + e.getMessage();
        }
    }

    // ★ 데이터를 문자열로 예쁘게 포장하는 헬퍼 메서드
    private String buildDataContext(List<RestaurantDto> restaurants, List<TourDto> tours) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== 맛집 목록 ===\n");
        for (RestaurantDto r : restaurants) {
            // [수정] 변수명에 맞게 getter 메서드 수정
            // r.getRestId() -> r.getId()
            // r.getRestName() -> r.getName()
            // r.getRestBestMenu() -> r.getBestMenu()
            sb.append(String.format("- ID:%d | 이름:%s | 종류:%s | 대표메뉴:%s\n",
                    r.getId(), r.getName(), r.getRestCategory(), r.getBestMenu()));
        }

        sb.append("\n=== 관광지 목록 ===\n");
        for (TourDto t : tours) {
            // [수정] 변수명에 맞게 getter 메서드 수정
            // t.getTourId() -> t.getId()
            // t.getTourName() -> t.getName()
            // t.getTourAddress() -> t.getAddress()
            // t.getTourSummary() -> t.getDescription() (DTO에 summary가 없고 description이 있음)
            sb.append(String.format("- ID:%d | 이름:%s | 주소:%s | 설명:%s\n",
                    t.getId(), t.getName(), t.getAddress(), t.getDescription()));
        }

        return sb.toString();
    }
}