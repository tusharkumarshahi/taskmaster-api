package com.example.taskmaster.dto;

import com.example.taskmaster.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdate {
    @NotNull
    private Status status;
}
