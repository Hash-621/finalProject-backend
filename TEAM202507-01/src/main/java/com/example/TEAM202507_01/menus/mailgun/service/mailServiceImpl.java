package com.example.TEAM202507_01.menus.mailgun.service;

import com.example.TEAM202507_01.menus.mailgun.dto.mailDto;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
//@RequiredArgsConstructor: final이 붙은 필드에 대한 생성자를 자동으로 만들어준다.

public class mailServiceImpl implements mailService {

    // 실제 메일을 발송해주는 스프링의 도구이다.
    // final을 붙여서 @RequiredArgsConstructor를 통해 스프링으로부터 주입받는다.
    private final JavaMailSenderImpl mailSender;

    // [첫 번째 메서드: 일반 메일 발송]
    //@RequestBody는 보통 컨트롤러에서 쓰는데, 여기에 있는 건 실수거나 불필요한 코드일 가능성이 높다. 동작엔 영향 없다
    public void sendMail(@RequestBody mailDto mailDto) {
        try { // 예외 처리 시작 (메일 전송은 실패할 수 있으니까)

            // 1. 빈 편지봉투(MimeMessage)를 만든다.
            // MimeMessage는 첨부파일이나 HTML 등 복잡한 내용을 담을 수 있는 객체이다.
            MimeMessage message = mailSender.createMimeMessage();

            // 2. 편지지를 다루기 쉽게 도와주는 도우미(Helper)를 만든다.
            // new MimeMessageHelper(메시지객체, 멀티파트여부, 인코딩)
            // true: 파일 첨부 등이 가능한 '멀티파트' 모드 사용.
            // "UTF-8": 한글이 깨지지 않게 인코딩 설정.
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 3. 수신자 설정 (DTO에서 꺼내옴)
            helper.setTo(mailDto.getTo());

            // 4. 제목 설정 (DTO에서 꺼내옴)
            helper.setSubject(mailDto.getSubject());

            // 5. 본문 설정 (DTO에서 꺼내옴)
            // 두 번째 파라미터 true: "이 내용은 HTML 태그가 포함되어 있다"는 뜻이다.
            //false로 하면 <h1>같은 태그가 글자 그대로 보인다.
            helper.setText(mailDto.getMessage(), true); // true는 HTML 형식임을 의미

            // 6. 전송!
            mailSender.send(message);

            // 7. 성공 로그 출력
            System.out.println("Mail sent successfully");
        } catch (MessagingException e) { // 메일 보내다가 에러가 나면(인터넷 끊김, 주소 오류 등) 여기서 잡는다.
            System.err.println("메일 전송 실패 : " + e.getMessage());
        }

    }

    // [두 번째 메서드: 아이디 찾기용 메일 발송]
    public void sendFindIdMail(String addr, String token) {
        // 보낼 내용을 HTML 형식의 문자열로 미리 만든다.
        // token 변수가 문자열 중간에 삽입된다.
        String text = "<h1>안녕하세요<h1><br><h3>아래의 인증 번호를 입력해주세요<h3><br><h1>" + token + "<h1><br>";
        try {
            // 위와 동일하게 메일 객체 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            //수신자 설정 (매개변수로 받은 addr)
            helper.setTo(addr);

            //제목은 고정된 값 사용
            helper.setSubject("[다잇슈 대전]아이디 찾기");

            // 만들어둔 HTML 내용(text)을 본문에 넣는다. HTML 모드 true.
            helper.setText(text, true); // true는 HTML 형식임을 의미

            //전송
            mailSender.send(message);
            System.out.println("메일 전송완료!");
        } catch (MessagingException e) {
            System.err.println("메일 전송 실패 : " + e.getMessage());
        }
    }
    @Value("${server.address}")
    String domain;
    @Value("${server.port}")
    String port;
    public void sendResetPwMail(String addr, String token) {

        String text = "<h1>안녕하세요<h1><br><h3>비밀 번호 변경 요청이 생성 되었습니다. 아래의 주소로 접속해주세요.<h3><br>"+
        domain + ":" + port + "/api/v1/user/resetPw?token=" + token +"&email="+addr+"<br>본인이 요청이 아닌경우 무시해주세요.3분간 유효합니다.";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(addr);
            helper.setSubject("[다잇슈 대전]비밀 번호 재설정");
            helper.setText(text, true);
            mailSender.send(message);
            System.out.println("메일 전송완료!");
        } catch (MessagingException e) {
            System.err.println("메일 전송 실패 : " + e.getMessage());
        }
    }

}
