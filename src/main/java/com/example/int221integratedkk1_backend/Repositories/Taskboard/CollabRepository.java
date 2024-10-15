package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollabRepository extends JpaRepository<Collaborator, String> {



    List<Collaborator> findByBoardId(@Param("boardId") String boardId);
    Optional<Collaborator> findByBoardIdAndCollaboratorId(String boardId, String collabId);

    boolean existsByBoardIdAndCollaboratorId(String boardId, String collaboratorId);
    List<Collaborator> findByCollaboratorId(String collaboratorId);
    boolean existsByBoardIdAndCollaboratorEmail(String boardId, String collaboratorEmail);
}
