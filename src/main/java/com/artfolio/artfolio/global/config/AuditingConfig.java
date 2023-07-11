package com.artfolio.artfolio.global.config;

import com.artfolio.artfolio.user.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return () -> Optional.of("authentication is null");
        }

        User user = (User) authentication.getPrincipal();
        return () -> Optional.of(user.getNickname());
    }
}
