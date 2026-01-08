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
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void saveAndFind() {
        Task t = Task.builder().title("Test").description("desc").status(Status.TODO).build();
        Task saved = repository.saveAndFlush(t);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        Task fetched = repository.findById(saved.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getTitle()).isEqualTo("Test");
    }
}
