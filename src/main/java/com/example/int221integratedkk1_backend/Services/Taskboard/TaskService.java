package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Exception.UnauthorizedException;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.StatusRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.TaskRepository;
import com.example.int221integratedkk1_backend.Services.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    public List<TaskDTO> getAllTasks(String boardId, List<String> filterStatuses, String sortBy, String sortDirection, String ownerId) {
        Sort.Direction direction = Sort.Direction.ASC;
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(direction, sortBy);
        List<TaskEntity> tasks;

        // Ensure the user owns the board
        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("User does not own the board"));

        if (filterStatuses != null && !filterStatuses.isEmpty()) {
            tasks = repository.findAllByStatusNamesAndBoardId(filterStatuses, boardId, sort);
        } else {
            tasks = repository.findAllByBoardId(boardId, sort);
        }
        return listMapper.mapList(tasks, TaskDTO.class);
    }

    public TaskEntity getTaskByIdAndBoard(Integer taskId, String boardId, String ownerId) {
        // Ensure the user owns the board
        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("User does not own the board"));

        return repository.findByIdAndBoardId(taskId, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found in this board"));
    }

    @Transactional
    public TaskEntity createTask(String boardId, TaskRequest taskRequest, String ownerId) throws Throwable {
        // Ensure the user owns the board
        BoardEntity board = boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("User does not own the board"));

        StatusEntity status = statusRepository.findByIdAndBoardId(taskRequest.getStatus(), boardId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found in this board"));

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(taskRequest.getTitle().trim());
        taskEntity.setDescription(taskRequest.getDescription() != null ? taskRequest.getDescription().trim() : null);
        taskEntity.setAssignees(taskRequest.getAssignees() != null ? taskRequest.getAssignees().trim() : null);
        taskEntity.setStatus(status);
        taskEntity.setBoard(board);

        return repository.save(taskEntity);
    }

    @Transactional
    public boolean updateTask(Integer id, String boardId, TaskRequest taskRequest, String ownerId) throws Throwable {
        TaskEntity task = repository.findByIdAndBoardId(id, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found in this board"));

        // Ensure the user owns the board
        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("User does not own the board"));

        Integer statusId = taskRequest.getStatus();
        StatusEntity statusEntity = statusRepository.findByIdAndBoardId(statusId, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found in this board"));

        task.setTitle(taskRequest.getTitle().trim());
        task.setDescription(taskRequest.getDescription() != null ? taskRequest.getDescription().trim() : null);
        task.setAssignees(taskRequest.getAssignees() != null ? taskRequest.getAssignees().trim() : null);
        task.setStatus(statusEntity);
        task.setUpdatedOn(ZonedDateTime.now().toOffsetDateTime());

        repository.save(task);
        return true;
    }

    @Transactional
    public boolean deleteTask(Integer id, String boardId, String ownerId) {
        TaskEntity task = repository.findByIdAndBoardId(id, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found in this board"));

        // Ensure the user owns the board
        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("User does not own the board"));

        repository.delete(task);
        return true;
    }
}
