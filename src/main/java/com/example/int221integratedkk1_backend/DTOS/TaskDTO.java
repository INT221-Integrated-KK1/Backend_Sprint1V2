package com.example.int221integratedkk1_backend.DTOS;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class TaskDTO {
    private int id;
    private String title;
    private String assignees;
    @ManyToOne
    @JoinColumn(name = "taskStatus", referencedColumnName = "statusId", nullable = false, columnDefinition = "varchar(255) default 'No Status'")
    private StatusEntity status;


}


