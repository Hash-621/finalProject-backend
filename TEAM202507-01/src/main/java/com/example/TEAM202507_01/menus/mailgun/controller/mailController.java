package com.example.TEAM202507_01.menus.mailgun.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import com.example.TEAM202507_01.menus.mailgun.dto.mailDto;
import com.example.TEAM202507_01.menus.mailgun.service.mailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/mail")
@AllArgsConstructor
public class mailController {
    private final mailService mailService;

    @PostMapping("/sendmail")
    public ResponseEntity<String> sendmail(@RequestBody mailDto mailDto) {
        try {
            mailService.sendMail(mailDto);
            return ResponseEntity.ok("Mail sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
