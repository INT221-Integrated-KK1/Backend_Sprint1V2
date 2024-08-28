package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.*;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.StatusRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository, TaskRepository taskRepository) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
    }

    public List<StatusEntity> getAllStatuses() {
        return statusRepository.findAll();
    }

    public StatusEntity getStatusById(int id) throws ItemNotFoundException {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));
    }

    @Transactional
    public StatusEntity createStatus(@Valid StatusEntity statusEntity) {
        if (statusRepository.findByName(statusEntity.getName()).isPresent()) {
            throw new DuplicateStatusException("Status name must be unique");
        }
        return statusRepository.save(statusEntity);
    }

    @Transactional
    public String updateStatus(int id, @Valid StatusEntity updatedStatus) throws ItemNotFoundException, DuplicateStatusException, UnManageStatusException {
        StatusEntity existingStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        // Check constraints for "No Status" and "Done"
        if ("No Status".equalsIgnoreCase(existingStatus.getName()) || existingStatus.getId() == 1 ||
                "Done".equalsIgnoreCase(existingStatus.getName()) || existingStatus.getId() == 7) {
            throw new UnManageStatusException("Cannot update or delete protected statuses");
        }

        Optional<StatusEntity> duplicateStatus = statusRepository.findByName(updatedStatus.getName().trim());
        if (duplicateStatus.isPresent() && duplicateStatus.get().getId() != existingStatus.getId()) {
            throw new DuplicateStatusException("Status name must be unique");
        }

        existingStatus.setName(updatedStatus.getName());
        existingStatus.setDescription(updatedStatus.getDescription());

        statusRepository.save(existingStatus);
        return "Status has been updated";
    }

    @Transactional
    public void deleteStatus(int id) throws ItemNotFoundException, UnManageStatusException {
        StatusEntity status = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        if ("No Status".equalsIgnoreCase(status.getName()) || status.getId() == 1 ||
                "Done".equalsIgnoreCase(status.getName()) || status.getId() == 7) {
            throw new UnManageStatusException("Cannot delete protected statuses");
        }

        statusRepository.delete(status);
    }

    @Transactional
    public int transferTasksAndDeleteStatus(int id, Integer transferToId) throws ItemNotFoundException, UnManageStatusException, InvalidTransferIdException {
        StatusEntity statusToDelete = statusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Status " + id + " not found"));

        if ("No Status".equalsIgnoreCase(statusToDelete.getName()) || statusToDelete.getId() == 1 ||
                "Done".equalsIgnoreCase(statusToDelete.getName()) || statusToDelete.getId() == 7) {
            throw new UnManageStatusException("Cannot delete protected statuses");
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
}
