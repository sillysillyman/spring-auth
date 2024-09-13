package com.example.springauth.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteUserRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String password;
}
