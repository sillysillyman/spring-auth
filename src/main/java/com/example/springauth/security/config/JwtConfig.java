package com.example.springauth.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Value("${jwt.authorization-header}")
    private String authorizationHeader;

    @Value("${jwt.refresh-header}")
    private String refreshHeader;
}
