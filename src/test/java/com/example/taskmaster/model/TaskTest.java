package com.example.taskmaster.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void builder_shouldCreateValidTask() {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .status(Status.TODO)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getStatus()).isEqualTo(Status.TODO);
        assertThat(task.getCreatedAt()).isEqualTo(now);
        assertThat(task.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1L, "Title", "Desc", Status.IN_PROGRESS, now, now);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getDescription()).isEqualTo("Desc");
        assertThat(task.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task();
        task.setId(2L);
        task.setTitle("New Title");
        task.setDescription("New Desc");
        task.setStatus(Status.DONE);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        assertThat(task.getId()).isEqualTo(2L);
        assertThat(task.getTitle()).isEqualTo("New Title");
        assertThat(task.getDescription()).isEqualTo("New Desc");
        assertThat(task.getStatus()).isEqualTo(Status.DONE);
        assertThat(task.getCreatedAt()).isEqualTo(now);
        assertThat(task.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_shouldWork() {
        LocalDateTime now = LocalDateTime.now();
        Task t1 = new Task(1L, "Title", "Desc", Status.TODO, now, now);
        Task t2 = new Task(1L, "Title", "Desc", Status.TODO, now, now);

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1L, "Title", "Desc", Status.TODO, now, now);

        String str = task.toString();

        assertThat(str).contains("Title");
        assertThat(str).contains("Desc");
        assertThat(str).contains("TODO");
    }
}
