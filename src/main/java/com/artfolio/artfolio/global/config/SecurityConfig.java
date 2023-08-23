package com.artfolio.artfolio.global.config;

import com.artfolio.artfolio.global.filter.JwtAuthenticationProcessingFilter;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .headers().frameOptions().disable()

                .and()

                .logout().logoutSuccessUrl("/")

                .and()

                // 세션 사용X
                .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 권한 관리 옵션
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/login", "/login/**", "/sock/**", "/sock", "/oauth2/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )

                .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
    }

}
