package com.example.springauth.domain.user.service;

import com.example.springauth.common.exception.auth.PasswordMismatchException;
import com.example.springauth.domain.user.dto.ChangePasswordRequestDto;
import com.example.springauth.domain.user.dto.DeleteUserRequestDto;
import com.example.springauth.domain.user.dto.UserResponseDto;
import com.example.springauth.domain.user.entity.User;
import com.example.springauth.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "UserService")
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        return UserResponseDto.from(userRepository.findByIdOrThrow(userId));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDto::from);
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto, User user) {
        if (!changePasswordRequestDto.getNewPassword().equals(
            changePasswordRequestDto.getConfirmedNewPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(),
            user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        log.info("현재 비밀번호: " + user.getPassword());
        user.updatePassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        log.info("새 비밀번호: " + user.getPassword());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(DeleteUserRequestDto deleteUserRequestDto, User user) {
        if (!passwordEncoder.matches(deleteUserRequestDto.getPassword(),
            user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        user.delete();
        userRepository.save(user);
    }
}
