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

class TaskRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_shouldCreateValidRequest() {
        TaskRequest request = TaskRequest.builder()
                .title("Test Task")
                .description("Description")
                .status(Status.TODO)
                .build();

        assertThat(request.getTitle()).isEqualTo("Test Task");
        assertThat(request.getDescription()).isEqualTo("Description");
        assertThat(request.getStatus()).isEqualTo(Status.TODO);
    }

    @Test
    void allArgsConstructor_shouldWork() {
        TaskRequest request = new TaskRequest("Title", "Desc", Status.IN_PROGRESS);

        assertThat(request.getTitle()).isEqualTo("Title");
        assertThat(request.getDescription()).isEqualTo("Desc");
        assertThat(request.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Title");
        request.setDescription("New Desc");
        request.setStatus(Status.DONE);

        assertThat(request.getTitle()).isEqualTo("New Title");
        assertThat(request.getDescription()).isEqualTo("New Desc");
        assertThat(request.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void validation_shouldFailForBlankTitle() {
        TaskRequest request = TaskRequest.builder()
                .title("")
                .description("Description")
                .status(Status.TODO)
                .build();

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("title is required"));
    }

    @Test
    void validation_shouldFailForNullTitle() {
        TaskRequest request = TaskRequest.builder()
                .title(null)
                .description("Description")
                .status(Status.TODO)
                .build();

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("title is required"));
    }

    @Test
    void validation_shouldFailForTooLongTitle() {
        String longTitle = "a".repeat(101);
        TaskRequest request = TaskRequest.builder()
                .title(longTitle)
                .description("Description")
                .status(Status.TODO)
                .build();

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validation_shouldFailForTooLongDescription() {
        String longDesc = "a".repeat(501);
        TaskRequest request = TaskRequest.builder()
                .title("Valid Title")
                .description(longDesc)
                .status(Status.TODO)
                .build();

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validation_shouldPassForValidRequest() {
        TaskRequest request = TaskRequest.builder()
                .title("Valid Task")
                .description("Valid description")
                .status(Status.TODO)
                .build();

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void equals_shouldWork() {
        TaskRequest r1 = new TaskRequest("Title", "Desc", Status.TODO);
        TaskRequest r2 = new TaskRequest("Title", "Desc", Status.TODO);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        TaskRequest request = new TaskRequest("Title", "Desc", Status.TODO);

        String str = request.toString();

        assertThat(str).contains("Title");
        assertThat(str).contains("Desc");
        assertThat(str).contains("TODO");
    }
}
