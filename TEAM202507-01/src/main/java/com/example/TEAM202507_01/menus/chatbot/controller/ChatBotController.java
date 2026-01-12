package com.example.TEAM202507_01.menus.chatbot.controller; // 1. 패키지 경로

// 2. [Imports] 서비스, 롬복, 스프링 웹 도구들을 가져옵니다.
import com.example.TEAM202507_01.menus.chatbot.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController // 3. 이 클래스는 JSON 데이터를 주고받는 API 컨트롤러입니다.
@RequestMapping("/api/v1/chatbot") // 4. "http://서버/api/v1/chatbot"으로 시작하는 주소는 다 이리로 오세요.
@RequiredArgsConstructor // 5. final 변수(geminiService)를 채워주는 생성자 자동 생성
public class ChatBotController {

    // 6. [Service Injection] 실제 AI 통신을 담당하는 서비스를 가져옵니다.
    private final GeminiService geminiService;

    // 7. [POST API] 사용자가 질문을 보낼 때 사용하는 주소 (/chat)
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        // 8. 프론트엔드에서 보낸 JSON 데이터 { "message": "맛집 추천해줘" } 에서 "message" 내용을 꺼냅니다.
        String userMessage = request.get("message");

        // 9. [Service Call] 서비스에게 질문을 넘기고 AI의 답변을 받아옵니다.
        // geminiService 안에 있는 getContents 메서드가 실제 일을 다 합니다.
        String aiResponse = geminiService.getContents(userMessage);

        // 10. [Return] AI의 답변을 다시 JSON { "response": "성심당 추천합니다!" } 형태로 포장해서 돌려줍니다.
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}
//
//질문 접수 (Controller):
//
//사용자가 "배고파, 맛집 추천 좀"이라고 입력하면 컨트롤러가 받습니다.
//
//데이터 조회 (Service):
//
//서비스는 AI에게 바로 물어보지 않고, 먼저 우리 DB에서 맛집 목록과 관광지 목록을 싹 긁어옵니다. (약 50개씩)
//
//프롬프트 조립 (Prompt Engineering):
//
//AI에게 보낼 편지를 씁니다.
//
//        "너는 대전 전문가야. 그리고 여기 우리 DB에 있는 맛집 리스트(성심당, 칼국수집...)랑 관광지 리스트(수목원, 엑스포...)가 있어. 이 데이터 안에서만 추천해줘."
//
//그리고 마지막에 사용자의 질문("배고파")을 붙입니다.
//
//AI 호출 (API Call):
//
//완성된 긴 편지를 구글 Gemini 서버로 보냅니다.
//
//답변 생성 및 반환:
//
//Gemini는 편지를 읽고 "아, 이 리스트 중에 성심당이 좋겠군. 성심당 어때요?"라고 답변을 만듭니다.
//
//서비스는 이 답변을 받아서 사용자에게 전달합니다.