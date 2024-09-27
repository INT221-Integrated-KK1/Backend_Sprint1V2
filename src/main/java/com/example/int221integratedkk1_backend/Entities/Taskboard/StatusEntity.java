package com.example.int221integratedkk1_backend.Entities.Taskboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Status", schema = "ITB-KK-V3")
public class StatusEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "statusId")
    private int id;

    @NotBlank(message = "Status name must not be null or empty")
    @Size(max = 50, message = "Name size must be between 0 and 50")
    @Column(name = "statusName", length = 50, nullable = false)
    private String name;

    @Size(max = 200, message = "Description size must be between 0 and 200")
    @Column(name = "statusDescription", length = 200)
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "boardId", nullable = false)
    private BoardEntity board;

    public void setName(String name) {
        if (name != null) {
            String trimmedName = name.trim();
            this.name = trimmedName.isEmpty() ? null : trimmedName;
        }
    }

    public void setDescription(String description) {
        if (description != null) {
            String trimmedDescription = description.trim();
            this.description = trimmedDescription.isEmpty() ? null : trimmedDescription;
        }
    }
}
