package com.example.taskmaster.service;

import com.example.taskmaster.dto.TaskRequest;
import com.example.taskmaster.dto.TaskResponse;
import com.example.taskmaster.model.Status;

import java.util.List;

public interface TaskService {
    List<TaskResponse> getAllTasks();
    TaskResponse getTask(Long id);
    TaskResponse createTask(TaskRequest request);
    TaskResponse updateTask(Long id, TaskRequest request);
    void deleteTask(Long id);
    TaskResponse updateStatus(Long id, Status status);

    // A complex method we will not test to keep coverage at target
    void complexUncoveredMethod();
}
