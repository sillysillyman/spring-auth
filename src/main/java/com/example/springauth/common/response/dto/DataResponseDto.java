package com.example.springauth.common.response.dto;

public record DataResponseDto<T>(int statusCode, String message, T data) {

}
