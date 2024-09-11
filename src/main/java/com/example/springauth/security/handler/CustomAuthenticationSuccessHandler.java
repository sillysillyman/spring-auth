package com.example.springauth.security.handler;

import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.entity.CustomUserDetails;
import com.example.springauth.domain.auth.service.AuthService;
import com.example.springauth.security.config.JwtConfig;
import com.example.springauth.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j(topic = "CustomAuthenticationSuccessHandler")
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authResult) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        log.info("로그인 성공: {}", userDetails.getUsername());
        log.info("Access Token: {}", accessToken);
        log.info("Refresh Token: {}", refreshToken);

        jwtUtil.addAccessTokenToHeader(response, accessToken);
        jwtUtil.addRefreshTokenToHeader(response, refreshToken);

        log.info("Access Token Header: {}", response.getHeader(jwtConfig.getAuthorizationHeader()));
        log.info("Refresh Token Header: {}", response.getHeader(jwtConfig.getRefreshHeader()));

        authService.assignRefreshToken(refreshToken, userDetails.user()); // Refresh Token 할당

        ResponseUtil.writeJsonResponse(response, HttpStatus.OK.value(), "로그인 성공");
    }
}
