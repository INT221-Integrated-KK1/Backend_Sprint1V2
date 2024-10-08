package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.BoardEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.*;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.BoardRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.StatusRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository, TaskRepository taskRepository, BoardRepository boardRepository) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
    }


    public List<StatusEntity> getStatusesByBoard(String boardId, String ownerId) throws UnauthorizedException {

//        boardRepository.findByIdAndOwnerId(boardId, ownerId)
//                .orElseThrow(() -> new ItemNotFoundException("Board not found or user does not an owner"));

        return statusRepository.findByBoard_Id(boardId);
    }


    public StatusEntity getStatusByIdAndBoard(int statusId, String boardId, String ownerId) throws ItemNotFoundException, UnauthorizedException {
//        boardRepository.findByIdAndOwnerId(boardId, ownerId)
//                .orElseThrow(() -> new ItemNotFoundException("Board not found or user does not an owner"));

        return statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status " + statusId + " not found in this board"));
    }


    @Transactional
    public StatusEntity createStatus(String boardId, String ownerId, @Valid StatusEntity statusEntity) {

        BoardEntity board = boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found or user does not an owner"));

        if (statusRepository.findByNameAndBoard_Id(statusEntity.getName(), boardId).isPresent()) {
            throw new DuplicateStatusException("Status name must be unique within the board");
        }

        statusEntity.setBoard(board);
        return statusRepository.save(statusEntity);
    }

    @Transactional
    public StatusEntity updateStatus(int id, String boardId, String ownerId, @Valid StatusEntity updatedStatus) throws ItemNotFoundException, DuplicateStatusException, UnManageStatusException {

        BoardEntity board = boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found or user does not an owner"));

        StatusEntity existingStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        if (isProtectedStatus(existingStatus)) {
            throw new UnManageStatusException("Cannot update No Status or Done");
        }

        Optional<StatusEntity> duplicateStatus = statusRepository.findByNameAndBoard_Id(updatedStatus.getName().trim(), boardId);
        if (duplicateStatus.isPresent() && duplicateStatus.get().getId() != existingStatus.getId()) {
            throw new DuplicateStatusException("Status name must be unique within the board");
        }

        existingStatus.setName(updatedStatus.getName());
        existingStatus.setDescription(updatedStatus.getDescription());

        return statusRepository.save(existingStatus);
    }


    @Transactional
    public void deleteStatus(int id, String boardId, String ownerId) throws ItemNotFoundException, UnManageStatusException, UnauthorizedException {
        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("Board not found or user does not an owner"));

        StatusEntity status = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        if (isProtectedStatus(status)) {
            throw new UnManageStatusException("Cannot delete No Status or Done");
        }

        statusRepository.delete(status);
    }

    @Transactional
    public int transferTasksAndDeleteStatus(int id, Integer transferToId, String boardId, String ownerId) throws ItemNotFoundException, UnManageStatusException, InvalidTransferIdException, UnauthorizedException {

        boardRepository.findByIdAndOwnerId(boardId, ownerId)
                .orElseThrow(() -> new UnauthorizedException("Board not found or user does not an owner"));

        StatusEntity statusToDelete = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        if (isProtectedStatus(statusToDelete)) {
            throw new UnManageStatusException("Cannot delete No Status or Done");
        }

        if (transferToId != null && transferToId.equals(id)) {
            throw new InvalidTransferIdException("Destination status for task transfer must be different from the current status");
        }

        List<TaskEntity> tasks = taskRepository.findByStatusId(id);
        if (!tasks.isEmpty() && transferToId != null) {
            StatusEntity transferToStatus = statusRepository.findById(transferToId)
                    .orElseThrow(() -> new ItemNotFoundException("The specified status for task transfer does not exist"));

            tasks.forEach(task -> task.setStatus(transferToStatus));
            taskRepository.saveAll(tasks);
        }

        statusRepository.delete(statusToDelete);
        return tasks.size();
    }


    private boolean isProtectedStatus(StatusEntity status) {
        return "No Status".equalsIgnoreCase(status.getName()) || "Done".equalsIgnoreCase(status.getName());
    }
}


