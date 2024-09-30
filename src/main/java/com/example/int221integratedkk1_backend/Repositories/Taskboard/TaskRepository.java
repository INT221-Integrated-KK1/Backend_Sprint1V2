package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    List<TaskEntity> findByStatusId(Integer statusId);

    Optional<TaskEntity> findByIdAndBoard_Id(Integer taskId, String boardId);

    @Query("select t from TaskEntity t where t.status.name in :status and t.board.id = :boardId")
    List<TaskEntity> findAllByStatusNamesAndBoardId(@Param("status") List<String> status, @Param("boardId") String boardId, Sort sort);

    @Query("select t from TaskEntity t where t.board.id = :boardId")
    List<TaskEntity> findAllByBoardId(@Param("boardId") String boardId, Sort sort);

}
