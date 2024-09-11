package com.example.springauth.security.filter;

import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.dto.LoginRequestDto;
import com.example.springauth.domain.auth.entity.CustomUserDetails;
import com.example.springauth.domain.auth.service.CustomUserDetailsService;
import com.example.springauth.security.handler.CustomAuthenticationFailureHandler;
import com.example.springauth.security.handler.CustomAuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final CustomUserDetailsService userDetailsService;
    private final Validator validator;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
        CustomUserDetailsService userDetailsService,
        CustomAuthenticationSuccessHandler successHandler,
        CustomAuthenticationFailureHandler failureHandler,
        Validator validator) {

        super.setAuthenticationManager(authenticationManager);
        super.setAuthenticationSuccessHandler(successHandler);
        super.setAuthenticationFailureHandler(failureHandler);
        this.userDetailsService = userDetailsService;
        this.validator = validator;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) {

        try {
            // 클라이언트로부터 받은 로그인 요청 데이터를 처리
            LoginRequestDto loginRequestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequestDto.class);

            String validationErrors = validateDto(loginRequestDto);
            if (!validationErrors.isEmpty()) {
                log.error("유효성 검증 실패: {}", validationErrors);
                ResponseUtil.writeJsonResponse(response, HttpStatus.BAD_REQUEST.value(),
                    validationErrors);
                return null;
            }

            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(
                loginRequestDto.getUsername());

            // 사용자 유무 확인
            if (userDetails == null) {
                log.error("사용자를 찾을 수 없습니다.");
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
            }

            // 사용자 상태 확인 (ACTIVE 상태가 아닐 경우 실패 처리)
            if (!userDetails.isEnabled()) {
                log.error("비활성화된 계정입니다. (Status: {})", userDetails.user().getStatus());
                throw new DisabledException("계정이 비활성화되었습니다.");
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                    loginRequestDto.getPassword(), userDetails.getAuthorities());

            // AuthenticationManager를 통해 인증 처리
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            log.error("로그인 시도 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    // DTO 유효성 검증
    private String validateDto(LoginRequestDto requestDto) {
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(requestDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<LoginRequestDto> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            return sb.toString().trim();
        }
        return "";
    }
}
