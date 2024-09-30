package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {

    Optional<StatusEntity> findByIdAndBoard_Id(Integer id, String boardId);

    Optional<StatusEntity> findByNameAndBoard_Id(String name, String boardId);

    List<StatusEntity> findByBoard_Id(String boardId);
}

