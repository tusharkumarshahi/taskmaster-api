package com.example.taskmaster.controller;

import com.example.taskmaster.dto.TaskRequest;
import com.example.taskmaster.dto.TaskResponse;
import com.example.taskmaster.dto.TaskStatusUpdate;
import com.example.taskmaster.model.Status;
import com.example.taskmaster.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TaskService service;

    @Test
    void getAll() throws Exception {
        when(service.getAllTasks()).thenReturn(List.of(TaskResponse.builder().id(1L).title("t").build()));
        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create() throws Exception {
        TaskRequest req = TaskRequest.builder().title("New").build();
        TaskResponse resp = TaskResponse.builder().id(5L).title("New").createdAt(LocalDateTime.now()).build();
        when(service.createTask(any())).thenReturn(resp);
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/5"));
    }

    @Test
    void updateStatus() throws Exception {
        TaskStatusUpdate update = new TaskStatusUpdate(Status.DONE);
        TaskResponse resp = TaskResponse.builder().id(2L).status(Status.DONE).build();
        when(service.updateStatus(eq(2L), eq(Status.DONE))).thenReturn(resp);
        mvc.perform(patch("/api/tasks/2/status").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
}
