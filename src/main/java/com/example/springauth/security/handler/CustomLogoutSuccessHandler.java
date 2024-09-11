package com.example.springauth.security.handler;

import com.example.springauth.common.exception.auth.InvalidRefreshTokenException;
import com.example.springauth.common.exception.auth.RefreshTokenMismatchException;
import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.service.AuthService;
import com.example.springauth.security.config.JwtConfig;
import com.example.springauth.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j(topic = "CustomLogoutSuccessHandler")
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuthService authService;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        // Access Token을 헤더에서 추출
        String accessToken = jwtUtil.resolveToken(
            request.getHeader(jwtConfig.getAuthorizationHeader()));

        log.info("Access Token: {}", accessToken);

        if (!jwtUtil.validateToken(accessToken)) {
            String refreshToken = request.getHeader(jwtConfig.getRefreshHeader());

            // 액세스 토큰이 없거나 만료된 경우, 리프레시 토큰으로 액세스 토큰 재발급
            try {
                authService.refreshAccessToken(response, refreshToken);
                log.info("새로운 Access Token 발급");
                accessToken = jwtUtil.resolveToken(
                    response.getHeader(jwtConfig.getAuthorizationHeader()));
            } catch (InvalidRefreshTokenException | RefreshTokenMismatchException e) {
                // 리프레시 토큰이 유효하지 않은 경우
                ResponseUtil.writeJsonResponse(response, HttpStatus.BAD_REQUEST.value(),
                    "유효하지 않은 리프레시 토큰입니다.");
                return;
            }
        }

        // Access Token 또는 새로 발급한 Access Token으로 로그아웃 처리
        authService.logout(accessToken);
        log.info("로그아웃 성공");
        ResponseUtil.writeJsonResponse(response, HttpStatus.OK.value(), "로그아웃 성공");
    }
}
