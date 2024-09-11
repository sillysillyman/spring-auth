package com.example.springauth.common.exception;

import com.example.springauth.common.exception.auth.InvalidAccessTokenException;
import com.example.springauth.common.exception.auth.InvalidRefreshTokenException;
import com.example.springauth.common.exception.auth.PasswordMismatchException;
import com.example.springauth.common.exception.auth.RefreshTokenMismatchException;
import com.example.springauth.common.exception.user.DuplicateEmailException;
import com.example.springauth.common.exception.user.DuplicateUsernameException;
import com.example.springauth.common.exception.user.UserNotFoundException;
import com.example.springauth.common.response.dto.MessageResponseDto;
import com.example.springauth.common.response.util.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponseDto> handleConstraintViolationException(
        ConstraintViolationException e) {

        log.error("제약 조건 위반 오류:", e);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<MessageResponseDto> handleDuplicateEmailException(
        DuplicateEmailException e) {

        log.error("이메일 중복 오류:", e);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<MessageResponseDto> handleDuplicateUsernameException(
        DuplicateUsernameException e) {

        log.error("사용자이름 중복 오류:", e);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<MessageResponseDto> handleInvalidAccessTokenException(
        InvalidAccessTokenException e) {

        log.error("유효하지 않은 액세스 토큰 오류:", e);
        return ResponseUtil.of(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<MessageResponseDto> handleInvalidRefreshTokenException(
        InvalidRefreshTokenException e) {

        log.error("유효하지 않은 리프레시 토큰 오류:", e);
        return ResponseUtil.of(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {

        log.error("DTO 유효성 검증 실패 오류:", e);

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError ->
            errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        e.getBindingResult().getGlobalErrors().forEach(globalError ->
            errors.put(globalError.getObjectName(), globalError.getDefaultMessage())
        );

        return ResponseUtil.of(HttpStatus.BAD_REQUEST, errors.toString());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<MessageResponseDto> handlePasswordMismatchException(
        PasswordMismatchException e) {

        log.error("비밀번호 불일치 오류:", e);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RefreshTokenMismatchException.class)
    public ResponseEntity<MessageResponseDto> handleRefreshTokenMismatchException(
        RefreshTokenMismatchException e) {

        log.error("리프레시 토큰 불일치 오류:", e);
        return ResponseUtil.of(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponseDto> handleUserNotFoundException(UserNotFoundException e) {
        log.error("사용자 조회 실패 오류:", e);
        return ResponseUtil.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
