package com.example.taskmaster.repository;

import com.example.taskmaster.model.Status;
import com.example.taskmaster.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void saveAndFind() {
        Task t = Task.builder().title("Test").description("desc").status(Status.TODO).build();
        Task saved = repository.save(t);
        assertThat(saved.getId()).isNotNull();
        Task fetched = repository.findById(saved.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getTitle()).isEqualTo("Test");
    }
}
