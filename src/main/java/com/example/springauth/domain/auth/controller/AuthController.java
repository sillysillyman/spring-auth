package com.example.springauth.domain.auth.controller;

import com.example.springauth.common.response.dto.MessageResponseDto;
import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.dto.SignupRequestDto;
import com.example.springauth.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "AuthController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(
        @Valid @RequestBody SignupRequestDto signupRequestDto) {

        authService.signup(signupRequestDto);
        return ResponseUtil.of(HttpStatus.CREATED, "회원가입에 성공했습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<MessageResponseDto> refreshAccessToken(HttpServletResponse response,
        @RequestHeader("Refresh") String refreshToken) {

        authService.refreshAccessToken(response, refreshToken);
        return ResponseUtil.of(HttpStatus.OK, "액세스 토큰 갱신에 성공했습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(
        @RequestHeader("Authorization") String accessToken) {

        log.info("Received Access Token: {}", accessToken);
        authService.logout(accessToken);
        return ResponseUtil.of(HttpStatus.OK, "로그아웃 성공");
    }
}
