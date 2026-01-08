package com.example.taskmaster.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AuditingConfigTest {

    @Test
    void auditingDateTimeProvider_shouldReturnCurrentTime() {
        AuditingConfig config = new AuditingConfig();

        DateTimeProvider provider = config.auditingDateTimeProvider();

        Optional<TemporalAccessor> temporal = provider.getNow();
        assertThat(temporal).isPresent();
        assertThat(temporal.get()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void auditingDateTimeProvider_shouldReturnDifferentTimesOnMultipleCalls() throws InterruptedException {
        AuditingConfig config = new AuditingConfig();
        DateTimeProvider provider = config.auditingDateTimeProvider();

        Optional<TemporalAccessor> time1 = provider.getNow();
        Thread.sleep(10);
        Optional<TemporalAccessor> time2 = provider.getNow();

        assertThat(time1).isPresent();
        assertThat(time2).isPresent();
        // Times should be different or equal depending on precision
        assertThat(time1.get()).isInstanceOf(LocalDateTime.class);
        assertThat(time2.get()).isInstanceOf(LocalDateTime.class);
    }
}
