package com.example.int221integratedkk1_backend.Entities.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Account.UsersEntity;
import com.example.int221integratedkk1_backend.Entities.Account.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Board", schema = "ITB-KK-V3")
public class BoardEntity {

    @Id
    @Column(name = "boardId", length = 10)
    private String id;

    @Column(name = "boardname", nullable = false, length = 120)
    private String boardName;

    @Column(name = "userId", length = 36, nullable = false)
    private String ownerId;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatusEntity> statuses;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE;

}
