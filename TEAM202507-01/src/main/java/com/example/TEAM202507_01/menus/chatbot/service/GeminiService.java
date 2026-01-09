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

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    private final RestaurantMapper restaurantMapper;
    private final TourMapper tourMapper;

    public String getContents(String prompt) {

        // [수정] 메서드 이름을 기존(findAll)로 롤백
        List<RestaurantDto> restaurants = restaurantMapper.findAll();
        List<TourDto> tours = tourMapper.findAll();

        // 2. 프롬프트 생성
        String enhancedPrompt = createEnhancedPrompt(prompt, restaurants, tours);

        // 3. Gemini API 요청 구조 생성
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", enhancedPrompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        // 4. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 5. API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // 6. 응답 파싱
            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentResponse = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
                    if (partsResponse != null && !partsResponse.isEmpty()) {
                        return (String) partsResponse.get(0).get("text");
                    }
                }
            }
            return "죄송합니다. 현재 AI가 응답을 생성할 수 없습니다.";

        } catch (Exception e) {
            log.error("Gemini API Error", e);
            return "AI 서버 통신 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // ★ [핵심] 면접관이 놀랄만한 "AI 플래너" 프롬프트 생성 메서드
    private String createEnhancedPrompt(String userQuestion, List<RestaurantDto> restaurants, List<TourDto> tours) {
        StringBuilder sb = new StringBuilder();

        // [시스템 페르소나 설정]
        sb.append("당신은 대전 여행 전문가이자 친절한 현지 가이드 '방방곡곡 AI'입니다.\n");
        sb.append("당신의 임무는 사용자에게 대전의 숨겨진 명소와 맛집을 소개하고, 상황에 맞는 최적의 여행 코스를 제안하는 것입니다.\n");
        sb.append("반드시 아래 제공된 [데이터베이스 정보] 내에서만 추천해야 하며, 없는 장소를 지어내지 마세요.\n\n");

        // [데이터 주입 - 맛집]
        sb.append("[데이터베이스 정보 - 맛집]\n");
        int limit = 0;
        for (RestaurantDto r : restaurants) {
            if (limit++ > 50) break;
            // RestaurantDto는 기존 메서드 그대로 사용
            sb.append(String.format("- (ID:%d) [%s] : %s / 대표메뉴: %s / 주소: %s\n",
                    r.getId(), r.getName(), r.getRestCategory(), r.getBestMenu(), r.getAddress()));
        }

        // [데이터 주입 - 관광지]
        sb.append("\n[데이터베이스 정보 - 관광지]\n");
        limit = 0;
        for (TourDto t : tours) {
            if (limit++ > 50) break;
            // [수정] getCategory() 오류 해결 -> getDescription()으로 변경 (또는 없으면 생략)
            sb.append(String.format("- (ID:%d) [%s] : 설명(%s) / 주소: %s\n",
                    t.getId(), t.getName(), t.getDescription(), t.getAddress()));
        }

        // [답변 가이드라인]
        sb.append("\n[답변 작성 가이드라인]\n");
        sb.append("1. **인사**: 사용자에게 친근하게 인사하고 질문에 공감해주세요. (이모지 사용 필수 😊)\n");
        sb.append("2. **맞춤형 추천**: 사용자의 질문에 맞춰 단순 나열이 아닌 '코스'를 제안해보세요. (예: 점심 -> 관광 -> 카페)\n");
        sb.append("3. **스토리텔링**: 왜 이곳을 추천하는지 이유를 매력적으로 설명해주세요. (예: '비 오는 날엔 이곳의 따뜻한 국물이 최고예요!')\n");
        sb.append("4. **링크 생성 규칙**: 장소 이름은 반드시 클릭 가능한 링크 형식으로 작성하세요.\n");
        sb.append("   - 맛집 링크 형식: `[가게이름](/restaurant/ID)` (예: [성심당](/restaurant/1))\n");
        sb.append("   - 관광지 링크 형식: `[장소이름](/tour/attraction?keyword=장소이름)` (예: [한밭수목원](/tour/attraction?keyword=한밭수목원))\n");
        sb.append("5. **가독성**: 중요한 내용은 **굵게** 표시하고, 목록 기능을 활용해 깔끔하게 보여주세요.\n");
        sb.append("6. **질문 유도**: 답변 마지막에는 다른 추천이 필요한지 되물어주세요.\n\n");

        sb.append("사용자 질문: \"").append(userQuestion).append("\"\n");
        sb.append("위 가이드라인에 맞춰 답변을 생성해주세요.");

        return sb.toString();
    }
}