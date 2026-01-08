package com.example.taskmaster.repository;

import com.example.taskmaster.config.TestAuditingConfig;
import com.example.taskmaster.model.Status;
import com.example.taskmaster.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestAuditingConfig.class)
class TaskAuditingTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void createdAtSet() {
        Task t = Task.builder().title("audit").status(Status.TODO).build();
        Task s = repository.saveAndFlush(t);
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }
}
