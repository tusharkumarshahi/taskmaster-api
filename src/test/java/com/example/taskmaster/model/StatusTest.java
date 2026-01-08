package com.example.taskmaster.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusTest {

    @Test
    void values_shouldContainAllStatuses() {
        Status[] statuses = Status.values();

        assertThat(statuses).hasSize(3);
        assertThat(statuses).contains(Status.TODO, Status.IN_PROGRESS, Status.DONE);
    }

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        assertThat(Status.valueOf("TODO")).isEqualTo(Status.TODO);
        assertThat(Status.valueOf("IN_PROGRESS")).isEqualTo(Status.IN_PROGRESS);
        assertThat(Status.valueOf("DONE")).isEqualTo(Status.DONE);
    }

    @Test
    void name_shouldReturnCorrectString() {
        assertThat(Status.TODO.name()).isEqualTo("TODO");
        assertThat(Status.IN_PROGRESS.name()).isEqualTo("IN_PROGRESS");
        assertThat(Status.DONE.name()).isEqualTo("DONE");
    }

    @Test
    void ordinal_shouldReturnCorrectOrder() {
        assertThat(Status.TODO.ordinal()).isEqualTo(0);
        assertThat(Status.IN_PROGRESS.ordinal()).isEqualTo(1);
        assertThat(Status.DONE.ordinal()).isEqualTo(2);
    }
}
