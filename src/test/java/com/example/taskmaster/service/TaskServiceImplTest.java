package com.example.taskmaster.service;

import com.example.taskmaster.dto.TaskRequest;
import com.example.taskmaster.exception.TaskNotFoundException;
import com.example.taskmaster.model.Status;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.repository.TaskRepository;
import com.example.taskmaster.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TaskServiceImpl(repository);
    }

    @Test
    void createAndGet() {
        Task t = Task.builder().id(1L).title("T").description("d").status(Status.TODO).build();
        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(t);

        var req = TaskRequest.builder().title("T").description("d").build();
        var res = service.createTask(req);
        assertThat(res.getId()).isEqualTo(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(t));
        var g = service.getTask(1L);
        assertThat(g.getTitle()).isEqualTo("T");
    }

    @Test
    void updateNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.updateTask(2L, TaskRequest.builder().title("x").build()));
    }

    @Test
    void deleteNotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(99L));
    }

    @Test
    void updateStatus() {
        Task t = Task.builder().id(3L).title("x").status(Status.TODO).build();
        when(repository.findById(3L)).thenReturn(Optional.of(t));
        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(t);
        var res = service.updateStatus(3L, Status.DONE);
        assertThat(res.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(Task.builder().id(1L).title("a").build()));
        var all = service.getAllTasks();
        assertThat(all).hasSize(1);
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {
        Task existing = Task.builder()
                .id(10L)
                .title("Old Title")
                .description("Old Desc")
                .status(Status.TODO)
                .build();

        Task updated = Task.builder()
                .id(10L)
                .title("New Title")
                .description("New Desc")
                .status(Status.IN_PROGRESS)
                .build();

        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(updated);

        TaskRequest request = TaskRequest.builder()
                .title("New Title")
                .description("New Desc")
                .status(Status.IN_PROGRESS)
                .build();

        var response = service.updateTask(10L, request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getDescription()).isEqualTo("New Desc");
        assertThat(response.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(repository).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    void deleteTask_shouldDeleteExistingTask() {
        when(repository.existsById(5L)).thenReturn(true);
        doNothing().when(repository).deleteById(5L);

        service.deleteTask(5L);

        verify(repository).existsById(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void getTask_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.getTask(999L));
    }

    @Test
    void updateStatus_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.updateStatus(777L, Status.DONE));
    }

    @Test
    void createTask_shouldSetDefaultStatus() {
        Task savedTask = Task.builder()
                .id(20L)
                .title("No Status Task")
                .description("Desc")
                .status(Status.TODO)
                .build();

        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(savedTask);

        TaskRequest request = TaskRequest.builder()
                .title("No Status Task")
                .description("Desc")
                .build();

        var response = service.createTask(request);

        assertThat(response.getStatus()).isEqualTo(Status.TODO);
    }
}
