package com.example.taskmaster.dto;

import com.example.taskmaster.model.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStatusUpdateTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_shouldCreateValidUpdate() {
        TaskStatusUpdate update = TaskStatusUpdate.builder()
                .status(Status.IN_PROGRESS)
                .build();

        assertThat(update.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void allArgsConstructor_shouldWork() {
        TaskStatusUpdate update = new TaskStatusUpdate(Status.DONE);

        assertThat(update.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        TaskStatusUpdate update = new TaskStatusUpdate();
        update.setStatus(Status.TODO);

        assertThat(update.getStatus()).isEqualTo(Status.TODO);
    }

    @Test
    void validation_shouldFailForNullStatus() {
        TaskStatusUpdate update = TaskStatusUpdate.builder()
                .status(null)
                .build();

        Set<ConstraintViolation<TaskStatusUpdate>> violations = validator.validate(update);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validation_shouldPassForValidStatus() {
        TaskStatusUpdate update = TaskStatusUpdate.builder()
                .status(Status.IN_PROGRESS)
                .build();

        Set<ConstraintViolation<TaskStatusUpdate>> violations = validator.validate(update);

        assertThat(violations).isEmpty();
    }

    @Test
    void equals_shouldWork() {
        TaskStatusUpdate u1 = new TaskStatusUpdate(Status.DONE);
        TaskStatusUpdate u2 = new TaskStatusUpdate(Status.DONE);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }

    @Test
    void toString_shouldContainStatus() {
        TaskStatusUpdate update = new TaskStatusUpdate(Status.IN_PROGRESS);

        String str = update.toString();

        assertThat(str).contains("IN_PROGRESS");
    }
}
