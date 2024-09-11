package com.example.springauth.domain.auth.service;

import com.example.springauth.common.exception.auth.InvalidAccessTokenException;
import com.example.springauth.common.exception.auth.InvalidRefreshTokenException;
import com.example.springauth.common.exception.auth.PasswordMismatchException;
import com.example.springauth.common.exception.auth.RefreshTokenMismatchException;
import com.example.springauth.domain.auth.dto.SignupRequestDto;
import com.example.springauth.domain.user.entity.User;
import com.example.springauth.domain.user.entity.User.UserRole;
import com.example.springauth.domain.user.entity.User.UserStatus;
import com.example.springauth.domain.user.repository.UserRepository;
import com.example.springauth.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AuthService")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void assignRefreshToken(String refreshToken, User user) {
        log.info("refresh token 할당: {}", refreshToken);
        user.assignRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        if (!signupRequestDto.getPassword().equals(signupRequestDto.getConfirmedPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        userRepository.validateEmailUniqueness(signupRequestDto.getEmail());
        userRepository.validateUsernameUniqueness(signupRequestDto.getUsername());

        User user = User.builder()
            .username(signupRequestDto.getUsername())
            .password(passwordEncoder.encode(signupRequestDto.getPassword()))
            .email(signupRequestDto.getEmail())
            .status(UserStatus.ACTIVE)
            .role(UserRole.ROLE_USER)
            .build();

        userRepository.save(user);
        user.setCreatedBy(user.getUsername());
        user.setLastModifiedBy(user.getUsername());
    }

    @Transactional
    public void refreshAccessToken(HttpServletResponse response, String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsernameOrThrow(username);

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenMismatchException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.generateAccessToken(username);
        jwtUtil.addAccessTokenToHeader(response, newAccessToken);
    }

    @Transactional
    public void logout(String accessToken) {
        if (!jwtUtil.validateToken(accessToken)) {
            log.error("유효하지 않은 액세스 토큰 오류: {}", accessToken);
            throw new InvalidAccessTokenException("유효하지 않은 액세스 토큰입니다.");
        }

        String username = jwtUtil.extractUsername(accessToken);
        User user = userRepository.findByUsernameOrThrow(username);

        user.assignRefreshToken(null);
    }
}
