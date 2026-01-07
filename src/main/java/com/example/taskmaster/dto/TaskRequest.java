package com.example.taskmaster.dto;

import com.example.taskmaster.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "title is required")
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private Status status;
}
