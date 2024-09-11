package com.example.springauth.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String password;
}
