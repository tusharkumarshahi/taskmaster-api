package com.example.taskmaster.repository;

import com.example.taskmaster.model.Status;
import com.example.taskmaster.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskAuditingTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void createdAtSet() {
        Task t = Task.builder().title("audit").status(Status.TODO).build();
        Task s = repository.save(t);
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }
}
