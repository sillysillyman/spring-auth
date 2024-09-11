package com.example.springauth.security.filter;

import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.entity.CustomUserDetails;
import com.example.springauth.domain.auth.service.AuthService;
import com.example.springauth.domain.auth.service.CustomUserDetailsService;
import com.example.springauth.security.config.JwtConfig;
import com.example.springauth.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JwtAuthorizationFilter")
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain)
        throws ServletException, IOException {

        // Authorization 헤더에서 액세스 토큰 추출
        String accessToken = jwtUtil.resolveToken(
            request.getHeader(jwtConfig.getAuthorizationHeader()));

        // 유효한 액세스 토큰인 경우
        if (jwtUtil.validateToken(accessToken)) {
            setAuthentication(request, accessToken);
        } else {
            String refreshToken = request.getHeader(jwtConfig.getRefreshHeader());

            // 유효한 리프레시 토큰인 경우
            if (jwtUtil.validateToken(refreshToken)) {
                // 새로운 액세스 토큰 발급
                authService.refreshAccessToken(response, refreshToken);

                log.info("새로운 Access Token 발급");

                // 새로운 액세스 토큰을 SecurityContext에 설정
                accessToken = jwtUtil.resolveToken(
                    response.getHeader(jwtConfig.getAuthorizationHeader()));
                setAuthentication(request, accessToken);
            } else {
                // 리프레시 토큰이 없거나 유효하지 않음 -> 401 응답 및 로그인 유도
                ResponseUtil.writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "유효하지 않은 토큰입니다.");
                return;
            }
        }

        // 다음 필터로 요청을 넘김
        chain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
