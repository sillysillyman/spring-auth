package com.example.springauth.common.exception.auth;

public class RefreshTokenMismatchException extends RuntimeException {

    public RefreshTokenMismatchException(String message) {
        super(message);
    }
}
