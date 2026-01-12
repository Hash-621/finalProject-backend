package com.example.TEAM202507_01.menus.chatbot.service; // 1. 패키지 경로

// 2. [Imports] 맛집/관광지 데이터 객체, HTTP 통신 도구 등을 가져옵니다.
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

@Slf4j // 3. 로그 출력을 위한 어노테이션
@Service // 4. "나는 비즈니스 로직을 처리하는 서비스야" (Bean 등록)
@RequiredArgsConstructor // 5. final 필드 자동 주입 생성자
public class GeminiService {

    // 6. [Config] application.properties에서 구글 API 키를 가져옵니다.
    @Value("${gemini.api-key}")
    private String apiKey;

    // 7. [Config] 구글 Gemini API 주소를 가져옵니다.
    @Value("${gemini.url}")
    private String apiUrl;

    // 8. [Tool] 외부 서버(구글)와 통신할 도구입니다.
    private final RestTemplate restTemplate;

    // 9. [DB] 우리 DB에 있는 맛집, 관광지 정보를 가져올 매퍼들입니다.
    private final RestaurantMapper restaurantMapper;
    private final TourMapper tourMapper;

    // 10. [Main Method] 사용자의 질문(prompt)을 받아서 AI 답변을 반환하는 핵심 함수
    public String getContents(String prompt) {

        // 11. [Data Fetching] DB에서 맛집과 관광지 목록을 전부 가져옵니다.
        // (주의: 데이터가 너무 많으면 성능 이슈가 있을 수 있으니 나중엔 페이징이 필요할 수 있습니다.)
        List<RestaurantDto> restaurants = restaurantMapper.findAll();
        List<TourDto> tours = tourMapper.findAll();

        // 12. [Prompt Engineering] 가져온 데이터와 사용자 질문을 합쳐서 "똑똑한 질문지"를 만듭니다.
        // 아래 createEnhancedPrompt 함수가 이 일을 합니다.
        String enhancedPrompt = createEnhancedPrompt(prompt, restaurants, tours);

        // 13. [JSON Build] Gemini API가 요구하는 복잡한 JSON 구조를 만듭니다.
        // 구조: { "contents": [ { "parts": [ { "text": "질문내용" } ] } ] }
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", enhancedPrompt); // 질문 내용 넣기
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents); // 최종 조립

        // 14. [Header] "나 JSON 보낼 거고, 여기 API 키 있어" 라고 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey); // 구글 API 키 필수

        // 15. [Packing] 헤더와 본문을 합쳐서 택배 상자(HttpEntity)를 만듭니다.
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 16. [Sending] 구글 서버로 요청을 발사합니다! (POST 방식)
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // 17. [Parsing] 구글에서 온 응답(JSON)을 해체해서 실제 답변 텍스트만 꺼냅니다.
            // 응답 구조가 복잡해서(candidates -> content -> parts -> text) 껍질을 여러 번 까야 합니다.
            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentResponse = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
                    if (partsResponse != null && !partsResponse.isEmpty()) {
                        // 드디어 찾았다! AI의 답변 텍스트를 반환합니다.
                        return (String) partsResponse.get(0).get("text");
                    }
                }
            }
            // 응답이 비어있으면 에러 메시지 반환
            return "죄송합니다. 현재 AI가 응답을 생성할 수 없습니다.";

        } catch (Exception e) {
            // 통신 중 에러(키 오류, 인터넷 끊김 등) 발생 시 로그 찍고 메시지 반환
            log.error("Gemini API Error", e);
            return "AI 서버 통신 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // ★ [핵심] 면접관이 놀랄만한 "AI 플래너" 프롬프트 생성 메서드
    // 사용자 질문에 우리 DB 데이터를 섞어서 AI에게 "이 데이터 안에서만 대답해"라고 가스라이팅(?) 하는 곳입니다.
    private String createEnhancedPrompt(String userQuestion, List<RestaurantDto> restaurants, List<TourDto> tours) {
        StringBuilder sb = new StringBuilder(); // 긴 문자열을 효율적으로 만들기 위한 도구

        // 1. [Persona] AI에게 역할을 부여합니다.
        sb.append("당신은 대전 여행 전문가이자 친절한 현지 가이드 '방방곡곡 AI'입니다.\n");
        sb.append("당신의 임무는 사용자에게 대전의 숨겨진 명소와 맛집을 소개하고, 상황에 맞는 최적의 여행 코스를 제안하는 것입니다.\n");
        // [중요] 거짓말(Hallucination) 방지: 없는 장소 지어내지 말라고 경고
        sb.append("반드시 아래 제공된 [데이터베이스 정보] 내에서만 추천해야 하며, 없는 장소를 지어내지 마세요.\n\n");

        // 2. [Context Injection - 맛집] DB에서 가져온 맛집 리스트를 텍스트로 변환해서 붙여넣습니다.
        sb.append("[데이터베이스 정보 - 맛집]\n");
        int limit = 0;
        for (RestaurantDto r : restaurants) {
            if (limit++ > 50) break; // 토큰 비용 절약을 위해 50개까지만 (실무에선 검색해서 추려야 함)
            // 예: - (ID:1) [성심당] : 빵집 / 대표메뉴: 튀김소보로 / 주소: 대전 중구...
            sb.append(String.format("- (ID:%d) [%s] : %s / 대표메뉴: %s / 주소: %s\n",
                    r.getId(), r.getName(), r.getRestCategory(), r.getBestMenu(), r.getAddress()));
        }

        // 3. [Context Injection - 관광지] 관광지 정보도 똑같이 붙여넣습니다.
        sb.append("\n[데이터베이스 정보 - 관광지]\n");
        limit = 0;
        for (TourDto t : tours) {
            if (limit++ > 50) break;
            sb.append(String.format("- (ID:%d) [%s] : 설명(%s) / 주소: %s\n",
                    t.getId(), t.getName(), t.getDescription(), t.getAddress()));
        }

        // 4. [Guide] 답변 형식을 지정해줍니다. (친절하게, 링크 걸어서)
        sb.append("\n[답변 작성 가이드라인]\n");
        sb.append("1. **인사**: 사용자에게 친근하게 인사하고 질문에 공감해주세요. (이모지 사용 필수 😊)\n");
        sb.append("2. **맞춤형 추천**: 사용자의 질문에 맞춰 단순 나열이 아닌 '코스'를 제안해보세요.\n");
        sb.append("3. **스토리텔링**: 추천 이유를 매력적으로 설명해주세요.\n");
        // [중요] 링크 생성 규칙: 프론트엔드 라우팅에 맞게 링크를 걸어달라고 지시합니다.
        sb.append("4. **링크 생성 규칙**: 장소 이름은 반드시 클릭 가능한 링크 형식으로 작성하세요.\n");
        sb.append("   - 맛집 링크 형식: `[가게이름](/restaurant/ID)` (예: [성심당](/restaurant/1))\n");
        sb.append("   - 관광지 링크 형식: `[장소이름](/tour/attraction?keyword=장소이름)`\n");
        sb.append("5. **가독성**: 중요한 내용은 **굵게** 표시하세요.\n\n");

        // 5. 마지막으로 실제 사용자의 질문을 붙입니다.
        sb.append("사용자 질문: \"").append(userQuestion).append("\"\n");
        sb.append("위 가이드라인에 맞춰 답변을 생성해주세요.");

        // 완성된 긴~ 프롬프트를 반환합니다.
        return sb.toString();
    }
}