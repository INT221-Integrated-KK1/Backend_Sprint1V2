package com.example.int221integratedkk1_backend.Entities.Taskboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@Entity
@Table(name = "Task", schema = "ITB-KK-V3")
public class TaskEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "taskId")
    private int id;
    @Basic
    @Column(name = "taskTitle", nullable = false, length = 100)
    private String title;
    @Basic
    @Column(name = "taskDescription", length = 500)
    private String description;
    @Basic
    @Column(name = "taskAssignees", length = 30)
    private String assignees;

    @Basic
    @Column(name = "createdOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime createdOn;

    @Basic
    @Column(name = "updatedOn", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "statusId", referencedColumnName = "statusId", nullable = false, columnDefinition = "varchar(255) default 'No Status'")
    private StatusEntity status;

    @ManyToOne
    @JoinColumn(name = "boardId", nullable = false)
    private BoardEntity board;


    public void setTitle(String title) {
        if (title != null) {

            String trimmedTitle = title.trim();

            if (trimmedTitle.isEmpty()) {
                this.title = null;
            } else {
                this.title = trimmedTitle;
            }
        }
    }

    public void setDescription(String description) {
        if (description != null) {

            String trimmedDescription = description.trim();

            if (trimmedDescription.isEmpty()) {
                this.description = null;
            } else {
                this.description = trimmedDescription;
            }
        }
    }

    public void setAssignees(String assignees) {
        if (assignees != null) {

            String trimmedAssignees = assignees.trim();

            if (trimmedAssignees.isEmpty()) {
                this.assignees = null;
            } else {
                this.assignees = trimmedAssignees;
            }
        }
    }

}
