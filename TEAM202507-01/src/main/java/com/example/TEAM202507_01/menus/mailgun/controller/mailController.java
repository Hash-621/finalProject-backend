package com.example.TEAM202507_01.menus.mailgun.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import com.example.TEAM202507_01.menus.mailgun.dto.mailDto;
import com.example.TEAM202507_01.menus.mailgun.service.mailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 컨트롤러임을 명시한다.
//   @Controller와 달리, 모든 메서드의 리턴값이 뷰(HTML 파일)가 아니라 데이터(JSON/String) 자체이다.

@RequestMapping("api/v1/mail") // 이 컨트롤러의 기본 주소를 설정한다.이 안의 모든 기능은 /api/v1/mail 로 시작한다.
@AllArgsConstructor //모든 필드값을 받는 생성자를 자동으로 만들어준다.
//  여기서는 mailService를 주입받기 위해 사용했다. (@RequiredArgsConstructor와 비슷한 효과)
public class mailController {
    // 연결 포인트: 서비스 인터페이스를 가져온다.
    // 실제로는 스프링이 mailServiceImpl 객체를 여기에 넣어준다 (의존성 주입).
    private final mailService mailService;

    //@PostMapping("/sendmail"): HTTP POST 방식으로 "/sendmail" 주소로 요청이 오면 이 메서드를 실행한다.
    //// 최종 주소: /api/v1/mail/sendmail
    @PostMapping("/sendmail")
    public ResponseEntity<String> sendmail(@RequestBody mailDto mailDto) {
        //// @RequestBody: 요청 보낼 때 본문(Body)에 있는 JSON 데이터를 mailDto 객체로 변환해서 가져와라
        //ResponseEntity<String>: 결과 데이터뿐만 아니라 HTTP 상태 코드(200 OK, 400 Bad Request 등)를 같이 보내기 위한 포장지다
        try {
            // 서비스에게 일을 시킨다. "가져온 dto 정보로 메일 보내줘"
            mailService.sendMail(mailDto);

            // 성공하면 200 OK 상태코드와 함께 메시지 반환
            return ResponseEntity.ok("Mail sent successfully");
        } catch (Exception e) {
            // 실패하면 400 Bad Request 상태코드와 함께 에러 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
