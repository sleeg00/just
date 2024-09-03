package com.example.just.Config;
import com.example.just.Service.KakaoService;
import com.example.just.jwt.JwtAccessDeniedHandler;
import com.example.just.jwt.JwtAuthenticationEntryPoint;
import com.example.just.jwt.JwtProvider;
import com.example.just.jwt.JwtSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private KakaoService kakaoService;
    @Autowired
    private final JwtProvider jwtProvider;
    @Autowired
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private String[] permitList={
            "/v2/**",
            "/v3/**",
            "/configuration",
            "/swagger*/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/test/**",
            "/actuator/**",
    };

    public SecurityConfig(
            JwtProvider jwtProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ){
        this.jwtProvider = jwtProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer2() {  //해당 URL은 필터 거치지 않겠다
        return (web -> web.ignoring().antMatchers("/api/**", "/h2-console","/swagger/**","/swagger-ui/**","/actuator/**"));

        //return (web -> web.ignoring().antMatchers("/test"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers(permitList).permitAll()
                .antMatchers("/test/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));
                //.anyRequest().permitAll()
    }
}
