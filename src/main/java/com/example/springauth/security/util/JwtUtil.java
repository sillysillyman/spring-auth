package com.example.springauth.security.util;

import com.example.springauth.security.config.JwtConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JwtUtil")
public final class JwtUtil {

    @Getter
    private final JwtConfig jwtConfig;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            token = resolveToken(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.error("토큰 만료 오류: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("유효하지 않은 서명 오류: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("잘못된 토큰 형식 오류: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 토큰 형식 오류: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 토큰 오류: {}", e.getMessage());
        }
        return false;
    }

    private Boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
            .getExpiration().before(new Date());
    }

    public String generateAccessToken(String username) {
        return jwtConfig.getTokenPrefix() + Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(
                new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(
                new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            accessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
            response.setHeader(jwtConfig.getAuthorizationHeader(), accessToken);
        }
    }

    public void addRefreshTokenToHeader(HttpServletResponse response, String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            response.setHeader(jwtConfig.getRefreshHeader(), refreshToken);
        }
    }

    public String resolveToken(String token) {
        if (StringUtils.hasText(token)) {
            try {
                // URL 디코딩을 통해 %20을 공백으로 변환
                token = URLDecoder.decode(token, StandardCharsets.UTF_8);
                log.info("디코딩된 토큰: {}", token);

                if (token.startsWith(jwtConfig.getTokenPrefix())) {
                    return token.substring(jwtConfig.getTokenPrefix().length()).trim();
                }
            } catch (Exception e) {
                log.error("토큰 디코딩 중 오류 발생: {}", e.getMessage());
            }
        }
        return token;
    }
}
