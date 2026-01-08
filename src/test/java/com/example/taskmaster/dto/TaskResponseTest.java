package com.example.taskmaster.dto;

import com.example.taskmaster.model.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskResponseTest {

    @Test
    void builder_shouldCreateValidResponse() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .status(Status.TODO)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getStatus()).isEqualTo(Status.TODO);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponse response = new TaskResponse(1L, "Title", "Desc", Status.IN_PROGRESS, now, now);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponse response = new TaskResponse();
        response.setId(2L);
        response.setTitle("New Title");
        response.setDescription("New Desc");
        response.setStatus(Status.DONE);
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getDescription()).isEqualTo("New Desc");
        assertThat(response.getStatus()).isEqualTo(Status.DONE);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponse r1 = new TaskResponse(1L, "Title", "Desc", Status.TODO, now, now);
        TaskResponse r2 = new TaskResponse(1L, "Title", "Desc", Status.TODO, now, now);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponse response = new TaskResponse(1L, "Title", "Desc", Status.TODO, now, now);

        String str = response.toString();

        assertThat(str).contains("Title");
        assertThat(str).contains("Desc");
        assertThat(str).contains("TODO");
    }
}
