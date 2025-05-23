package com.example.just.Config;

import com.example.just.jwt.JwtAccessDeniedHandler;
import com.example.just.jwt.JwtAuthenticationEntryPoint;
import com.example.just.jwt.JwtProvider;
import com.example.just.jwt.JwtSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ✅ @PreAuthorize 사용 가능
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final String[] permitList = {
            "/v3/api-docs/**",  // ✅ OpenAPI JSON 문서 허용
            "/v3/api-docs",
            "/swagger-ui/**",  // ✅ Swagger UI 허용
            "/swagger-ui.html",
            "/api-docs/**",
            "/api/**/send",
            "/api/**/token",
            "/actuator/**",
            "/api/**",
            "/api/posts/**"
    };


    public SecurityConfig(
            JwtProvider jwtProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.jwtProvider = jwtProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/**",
                "/api/posts/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ✅ CSRF 비활성화
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitList).permitAll()  // ✅ 허용 목록 적용
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/v3/**").permitAll()
                        .requestMatchers("/test/**").hasRole("USER")  // ✅ USER 권한 필요
                        .anyRequest().authenticated())
                .apply(new JwtSecurityConfig(jwtProvider));  // ✅ JWT 설정 적용

        return http.build();
    }
}
