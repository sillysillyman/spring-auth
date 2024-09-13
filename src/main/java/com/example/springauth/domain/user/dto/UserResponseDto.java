package com.example.springauth.domain.user.dto;

import com.example.springauth.domain.user.entity.User;

public record UserResponseDto(String username, String email) {

    public static UserResponseDto from(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자는 null일 수 없습니다.");
        }

        return new UserResponseDto(user.getUsername(), user.getEmail());
    }
}
