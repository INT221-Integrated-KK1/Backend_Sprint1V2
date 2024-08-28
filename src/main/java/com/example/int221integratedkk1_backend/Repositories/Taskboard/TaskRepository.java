package com.example.int221integratedkk1_backend.Repositories.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    List<TaskEntity> findByStatusId(int statusId);

    @Query("select t from TaskEntity t join StatusEntity s on s.id = t.status.id where t.status.name in :status ")
    List<TaskEntity> findAllByStatusNames(@Param("status") List<String> status, Sort sort);


}
