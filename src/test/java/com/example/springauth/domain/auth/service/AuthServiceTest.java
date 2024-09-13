package com.example.springauth.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.springauth.common.exception.auth.InvalidAccessTokenException;
import com.example.springauth.common.exception.auth.InvalidRefreshTokenException;
import com.example.springauth.common.exception.auth.PasswordMismatchException;
import com.example.springauth.common.exception.auth.RefreshTokenMismatchException;
import com.example.springauth.domain.auth.dto.SignupRequestDto;
import com.example.springauth.domain.user.entity.User;
import com.example.springauth.domain.user.repository.UserRepository;
import com.example.springauth.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = mock(User.class);

        when(user.getRefreshToken()).thenReturn("valid_refresh_token");
        when(userRepository.findByUsernameOrThrow(anyString())).thenReturn(user);
    }

    @Test
    void testAssignRefreshToken() {
        // Given
        String refreshToken = "valid_refresh_token";

        // When
        authService.assignRefreshToken(refreshToken, user);

        // Then
        verify(userRepository).save(user);
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @Test
    void testSignup_Success() {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser", "password", "password",
            "test@example.com");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        authService.signup(signupRequestDto);

        // Then
        verify(userRepository).save(any(User.class));
        verify(userRepository).validateEmailUniqueness(signupRequestDto.getEmail());
        verify(userRepository).validateUsernameUniqueness(signupRequestDto.getUsername());
    }

    @Test
    void testSignup_PasswordMismatch() {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser", "password",
            "differentPassword", "test@example.com");

        // When-Then
        assertThrows(PasswordMismatchException.class, () -> authService.signup(signupRequestDto));
    }

    @Test
    void testRefreshAccessToken_Success() {
        // Given
        String refreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token";
        String username = "testUser";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);
        when(jwtUtil.generateAccessToken(username)).thenReturn(newAccessToken);
        when(user.getRefreshToken()).thenReturn(refreshToken);

        // When
        authService.refreshAccessToken(response, refreshToken);

        // Then
        verify(jwtUtil).addAccessTokenToHeader(response, newAccessToken);
    }

    @Test
    void testRefreshAccessToken_InvalidToken() {
        // Given
        String refreshToken = "invalid_refresh_token";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        // When-Then
        assertThrows(InvalidRefreshTokenException.class,
            () -> authService.refreshAccessToken(response, refreshToken));
    }

    @Test
    void testRefreshAccessToken_TokenMismatch() {
        // Given
        String refreshToken = "valid_refresh_token";
        String username = "testUser";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);
        // RefreshToken이 일치하지 않는 경우
        when(user.getRefreshToken()).thenReturn("different_refresh_token");

        // When-Then
        assertThrows(RefreshTokenMismatchException.class,
            () -> authService.refreshAccessToken(response, refreshToken));
    }

    @Test
    void testLogout_Success() {
        // Given
        String accessToken = "valid_access_token";
        String username = "testUser";

        when(jwtUtil.validateToken(accessToken)).thenReturn(true);
        when(jwtUtil.extractUsername(accessToken)).thenReturn(username);
        when(userRepository.findByUsernameOrThrow(username)).thenReturn(user);

        doAnswer(invocation -> {
            when(user.getRefreshToken()).thenReturn(null);
            return null;
        }).when(user).assignRefreshToken(null);

        // When
        authService.logout(accessToken);

        // Then
        verify(userRepository).findByUsernameOrThrow(username);
        assertNull(user.getRefreshToken());
    }

    @Test
    void testLogout_InvalidToken() {
        // Given
        String accessToken = "invalid_access_token";

        when(jwtUtil.validateToken(accessToken)).thenReturn(false);

        // When-Then
        assertThrows(InvalidAccessTokenException.class, () -> authService.logout(accessToken));
    }
}
