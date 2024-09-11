package com.example.springauth.domain.user.dto;

import com.example.springauth.domain.user.entity.User;

public record UserResponseDto(String username, String email) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getUsername(), user.getEmail());
    }
}
