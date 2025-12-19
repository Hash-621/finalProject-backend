package com.example.TEAM202507_01.alramo.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class AlramoController {
    private final AlramoService alramoService;
    @GetMapping("/test")
    public ResponseEntity<String> test() {
//        alramoService.sendNewPostNotification();
        return ResponseEntity.ok("This is a test");
    }
}
