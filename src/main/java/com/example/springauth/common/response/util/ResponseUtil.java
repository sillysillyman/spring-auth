package com.example.springauth.common.response.util;

import com.example.springauth.common.response.dto.DataResponseDto;
import com.example.springauth.common.response.dto.MessageResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class ResponseUtil {

    public static ResponseEntity<MessageResponseDto> of(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
            .body(new MessageResponseDto(httpStatus.value(), message));
    }

    public static <T> ResponseEntity<DataResponseDto<T>> of(HttpStatus httpStatus, String message,
        T data) {
        return ResponseEntity.status(httpStatus)
            .body(new DataResponseDto<>(httpStatus.value(), message, data));
    }

    public static void writeJsonResponse(HttpServletResponse response, int httpStatus,
        String message) throws IOException {

        response.setStatus(httpStatus);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String result = new ObjectMapper().writeValueAsString(
            new MessageResponseDto(httpStatus, message)
        );

        response.getWriter().write(result);
    }
}
