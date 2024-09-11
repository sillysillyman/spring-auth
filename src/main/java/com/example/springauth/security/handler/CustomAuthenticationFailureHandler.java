package com.example.springauth.security.handler;

import com.example.springauth.common.response.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j(topic = "CustomAuthenticationFailureHandler")
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException e) throws IOException {

        log.info("로그인 실패: {}", e.getMessage());
        int status = switch (e) {
            case BadCredentialsException ignored -> HttpStatus.UNAUTHORIZED.value();
            case DisabledException ignored -> HttpStatus.FORBIDDEN.value();
            case LockedException ignored -> HttpStatus.LOCKED.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };

        ResponseUtil.writeJsonResponse(response, status, e.getMessage());
    }
}
