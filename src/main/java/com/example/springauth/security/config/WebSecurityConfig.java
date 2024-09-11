package com.example.springauth.security.config;

import com.example.springauth.domain.auth.service.CustomUserDetailsService;
import com.example.springauth.security.filter.JwtAuthenticationFilter;
import com.example.springauth.security.filter.JwtAuthorizationFilter;
import com.example.springauth.security.handler.CustomAuthenticationFailureHandler;
import com.example.springauth.security.handler.CustomAuthenticationSuccessHandler;
import com.example.springauth.security.handler.CustomLogoutSuccessHandler;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CorsFilter corsFilter;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final Validator validator;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), userDetailsService,
            successHandler, failureHandler, validator);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // JWT 사용 시 CSRF 보호 비활성화
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음 (JWT 사용)
            // 기본 폼 로그인을 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 로그아웃 설정 (헤더 기반)
            .logout(logout -> logout.logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler))  // 커스텀 로그아웃 핸들러 적용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()  // 인증 없이 접근 가능
                .anyRequest().authenticated())  // 그 외 모든 요청은 인증 필요
            .addFilter(corsFilter) // CORS 필터
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터
            .addFilterAfter(jwtAuthorizationFilter,
                UsernamePasswordAuthenticationFilter.class);  // JWT 인가 필터

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
