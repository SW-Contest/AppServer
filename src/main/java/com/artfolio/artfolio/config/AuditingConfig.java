package com.artfolio.artfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        /* TODO
           [ 임시 코드 ]
           OAuth 적용시 사용자 이름(또는 아이디)를 빼오도록 리팩터링 예정
        */

        return () -> Optional.of("test");
    }
}
