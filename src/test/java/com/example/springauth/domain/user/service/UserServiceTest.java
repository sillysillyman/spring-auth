package com.example.springauth.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.springauth.common.exception.auth.PasswordMismatchException;
import com.example.springauth.common.exception.user.UserNotFoundException;
import com.example.springauth.domain.user.dto.ChangePasswordRequestDto;
import com.example.springauth.domain.user.dto.DeleteUserRequestDto;
import com.example.springauth.domain.user.entity.TestUserBuilder;
import com.example.springauth.domain.user.entity.User;
import com.example.springauth.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@Slf4j(topic = "UserServiceTest")
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // TestUserBuilder로 user 생성 (id 포함)
        user = TestUserBuilder.createUserWithId(1L);

        // userRepository에 대한 Mock 설정
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByIdOrThrow(1L)).thenReturn(user);
    }

    @Test
    void testGetUser_Success() {
        assertNotNull(user, "User 객체가 null입니다. setUp() 메서드를 확인하세요.");

        var userResponseDto = userService.getUser(1L);

        assertNotNull(userResponseDto);
        assertEquals("testUser", userResponseDto.username());

        // findByIdOrThrow 호출 여부 검증
        verify(userRepository).findByIdOrThrow(1L);
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findByIdOrThrow(anyLong()))
            .thenThrow(new UserNotFoundException("사용자를 찾을 수 없습니다."));

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void testChangePassword_Success() {
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto(
            "currentPassword", "newPassword1!", "newPassword1!"
        );

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        // 비밀번호 변경
        userService.changePassword(changePasswordRequestDto, user);

        // Mock 메서드 호출 검증
        verify(passwordEncoder).encode("newPassword1!");
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto(
            "currentPassword", "newPassword1!", "differentPassword"
        );

        assertThrows(PasswordMismatchException.class,
            () -> userService.changePassword(changePasswordRequestDto, user));
    }

    @Test
    void testDeleteUser_Success() {
        DeleteUserRequestDto deleteUserRequestDto = new DeleteUserRequestDto("currentPassword");

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userService.deleteUser(deleteUserRequestDto, user);

        assertEquals(User.UserStatus.DELETED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_PasswordMismatch() {
        DeleteUserRequestDto deleteUserRequestDto = new DeleteUserRequestDto("wrongPassword");

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(PasswordMismatchException.class,
            () -> userService.deleteUser(deleteUserRequestDto, user));
    }
}