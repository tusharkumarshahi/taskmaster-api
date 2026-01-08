package com.example.taskmaster.config;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class TestAuditingConfig {

    @Bean
    public AuditorAware<String> testAuditorAware() {
        return () -> Optional.of("test-user");
    }
}
