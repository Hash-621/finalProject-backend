package com.example.TEAM202507_01.user.controller;

import com.example.TEAM202507_01.config.helper.CookieHelper;
import com.example.TEAM202507_01.config.jwt.TokenDto;
import com.example.TEAM202507_01.user.dto.CreateUserDto;
import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.dto.UserSignInDto;
import com.example.TEAM202507_01.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final CookieHelper cookieHelper;

    // 1. 전체 회원 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<UserDto>> getUserList() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 2. 회원가입
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody CreateUserDto user) {
        userService.join(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/{id}").buildAndExpand(user.getLoginId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    // 3. 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> signIn(@RequestBody UserSignInDto signInDto) {
        String token = userService.createToken(signInDto);

        // 쿠키에 토큰 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Set-Cookie", cookieHelper.makeJwtCookie(token));

        return new ResponseEntity<>(TokenDto.builder().token(token).build(), httpHeaders, HttpStatus.OK);
    }

    // 4. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> signOut() {
        HttpHeaders httpHeaders = new HttpHeaders();
        cookieHelper.deleteJwtCookie(httpHeaders);
        return ResponseEntity.ok().headers(httpHeaders).body(null);
    }

    // 5. 회원 정보 조회
    @GetMapping("/{loginId}")
    public ResponseEntity<UserDto> retrieve(@PathVariable String loginId) {
        return ResponseEntity.ok(userService.findById(loginId));
    }

    // 6. 회원 정보 수정
    @PutMapping("/{loginId}")
    public ResponseEntity<Void> update(@PathVariable String loginId, @RequestBody UserDto user) {
        user.setLoginId(loginId);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    // 7. 회원 탈퇴
    @DeleteMapping("/{loginId}")
    public ResponseEntity<Void> delete(@PathVariable String loginId) {
        userService.delete(loginId);
        return ResponseEntity.ok().build();
    }
}