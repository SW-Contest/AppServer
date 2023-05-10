package com.artfolio.artfolio.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /* TODO
           개발 편의성을 위해 시큐리티 기본 작동 중단
        */

        return http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(CorsUtils::isPreFlightRequest)
                                .permitAll()
                                .anyRequest()
                                .permitAll()
                )
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(false);
        configuration.setAllowedOrigins(List.of("*"));

        // 메서드 허용
        configuration.setAllowedMethods(
                List.of(HttpMethod.POST.name(), HttpMethod.GET.name(),
                        HttpMethod.PUT.name(), HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name())
        );

        /*
        // 요청헤더 허용
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", ...)
        );
        // 응답 헤더 허용
        configuration.setExposedHeaders(
                Arrays.asList("Content-Type", ...)
        );
         */

        UrlBasedCorsConfigurationSource source
                = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean bean
                = new FilterRegistrationBean(new CorsFilter(source));

        bean.setOrder(0);
        return source;
    }
}
