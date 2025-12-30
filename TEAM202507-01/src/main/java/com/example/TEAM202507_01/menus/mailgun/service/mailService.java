package com.example.TEAM202507_01.menus.mailgun.service;

import com.example.TEAM202507_01.menus.mailgun.dto.mailDto;
import com.example.TEAM202507_01.user.dto.ResetPasswordDto;
import jakarta.mail.MessagingException;

public interface mailService {
    // 인터페이스이므로 몸통({ ... })이 없다. 구현은 implements를 받은 클래스가 해야 한다.

    // 1. 일반 메일 발송 기능 정의
    // mailDto 객체를 받아서 메일을 보낸다.
    void sendMail(mailDto mailDto);

    // 2. 아이디 찾기 인증 메일 발송 기능 정의
    // 이메일 주소(addr)와 인증 번호(token)를 받아서 보낸다.
    void sendFindIdMail(String addr,String token);

    void sendResetPwMail(String addr,String token);
}


///왜 인터페이스를 썼는가?
/// 유연성: 나중에 메일 보내는 방식이 바뀌더라도(mailServiceImpl 코드를 갈아엎더라도),
//이를 호출하는 Controller 코드는 수정할 필요가 없게 하기 위함이다. (객체지향의 다형성 활용)