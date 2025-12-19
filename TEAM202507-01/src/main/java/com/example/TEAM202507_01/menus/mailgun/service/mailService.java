package com.example.TEAM202507_01.menus.mailgun.service;

import com.example.TEAM202507_01.menus.mailgun.dto.mailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class mailService {

    private final JavaMailSenderImpl mailSender;

    public void sendMail(mailDto mailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(mailDto.getTo());
        helper.setSubject(mailDto.getSubject());
        helper.setText(mailDto.getMessage(), true); // true는 HTML 형식임을 의미

        mailSender.send(message);
    }
}
