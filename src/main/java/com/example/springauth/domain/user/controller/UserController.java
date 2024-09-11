package com.example.springauth.domain.user.controller;

import com.example.springauth.common.response.dto.DataResponseDto;
import com.example.springauth.common.response.dto.MessageResponseDto;
import com.example.springauth.common.response.util.ResponseUtil;
import com.example.springauth.domain.auth.entity.CustomUserDetails;
import com.example.springauth.domain.user.dto.ChangePasswordRequestDto;
import com.example.springauth.domain.user.dto.DeleteUserRequestDto;
import com.example.springauth.domain.user.dto.UserResponseDto;
import com.example.springauth.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<DataResponseDto<UserResponseDto>> getUser(@PathVariable Long userId) {
        return ResponseUtil.of(HttpStatus.OK, "사용자 조회에 성공했습니다.", userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<DataResponseDto<Page<UserResponseDto>>> getAllUsers(
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseUtil.of(HttpStatus.OK, "사용자 목록 조회에 성공했습니다.",
            userService.getAllUsers(pageable));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<MessageResponseDto> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.changePassword(changePasswordRequestDto, userDetails.user());
        return ResponseUtil.of(HttpStatus.OK, "비밀번호 변경에 성공했습니다.");
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponseDto> deleteUser(
        @Valid @RequestBody DeleteUserRequestDto deleteUserRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.deleteUser(deleteUserRequestDto, userDetails.user());
        return ResponseUtil.of(HttpStatus.OK, "회원 탈퇴에 성공했습니다.");
    }
}
