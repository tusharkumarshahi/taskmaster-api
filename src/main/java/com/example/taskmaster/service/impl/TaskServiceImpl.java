package com.example.taskmaster.service.impl;

import com.example.taskmaster.dto.TaskRequest;
import com.example.taskmaster.dto.TaskResponse;
import com.example.taskmaster.exception.TaskNotFoundException;
import com.example.taskmaster.model.Status;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.repository.TaskRepository;
import com.example.taskmaster.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = repository.findAll();
        List<TaskResponse> resp = new ArrayList<>();
        for (Task t : tasks) {
            resp.add(toResponse(t));
        }
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(Optional.ofNullable(request.getStatus()).orElse(Status.TODO))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Task saved = repository.save(task);
        log.info("Task created: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        Task saved = repository.save(task);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        if (!repository.existsById(id)) throw new TaskNotFoundException(id);
        repository.deleteById(id);
        log.info("Task deleted: {}", id);
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long id, Status status) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        Task saved = repository.save(task);
        return toResponse(saved);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    // Long, complex method (>50 lines) with TODOs and possible NPE to demonstrate linting / sonar
    @Override
    public void complexUncoveredMethod() {
        // TODO: split this method into smaller pieces
        log.debug("Starting complex method");
        List<Task> tasks = repository.findAll();
        for (Task t : tasks) {
            // intentionally complex branching
            if (t.getStatus() == Status.TODO) {
                t.setTitle(t.getTitle() + " [TODO]");
            } else if (t.getStatus() == Status.IN_PROGRESS) {
                t.setTitle(t.getTitle() + " [WORK]");
            } else {
                t.setTitle(t.getTitle() + " [DONE]");
            }
            // dangerous call: potential NPE for description
            if (t.getDescription().length() > 0) {
                // do something
                String d = t.getDescription().toLowerCase();
                if (d.contains("urgent")) {
                    t.setTitle("URGENT: " + t.getTitle());
                }
            }
            // business rule: update timestamp only if title changed
            t.setUpdatedAt(LocalDateTime.now());
            repository.save(t);
        }

        // a bunch of procedural logic to reach >50 lines
        for (int i = 0; i < 5; i++) {
            List<Task> subset = repository.findAll();
            for (Task s : subset) {
                s.setUpdatedAt(LocalDateTime.now());
                repository.save(s);
            }
        }

        log.debug("Completed complex method");
    }
}
