package com.example.int221integratedkk1_backend.Services.Taskboard;

import com.example.int221integratedkk1_backend.DTOS.TaskDTO;
import com.example.int221integratedkk1_backend.DTOS.TaskRequest;
import com.example.int221integratedkk1_backend.Entities.Taskboard.StatusEntity;
import com.example.int221integratedkk1_backend.Entities.Taskboard.TaskEntity;
import com.example.int221integratedkk1_backend.Exception.ItemNotFoundException;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.StatusRepository;
import com.example.int221integratedkk1_backend.Repositories.Taskboard.TaskRepository;
import com.example.int221integratedkk1_backend.Services.ListMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TaskService {

    private static final Logger LOGGER = Logger.getLogger(TaskService.class.getName());

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StatusRepository statusRepository;

    public List<TaskDTO> getAllTasks(List<String> filterStatuses, String sortBy, String sortDirection) {
        List<TaskEntity> tasks;
        Sort.Direction direction = null;
        if (sortDirection != null && !sortDirection.trim().isEmpty()) {
            String directionUpper = sortDirection.trim().toUpperCase();
            if ("ASC".equals(directionUpper)) {
                direction = Sort.Direction.ASC;
            } else if ("DESC".equals(directionUpper)) {
                direction = Sort.Direction.DESC;
            } else {
                throw new IllegalArgumentException("Invalid value for sortDirection: " + sortDirection);
            }
        } else {
            direction = Sort.Direction.ASC;
        }

        Sort sort = Sort.by(direction, sortBy);

        if (filterStatuses != null && !filterStatuses.isEmpty()) {
            tasks = repository.findAllByStatusNames(filterStatuses, sort);
            LOGGER.info("Filtered tasks by statuses: " + filterStatuses);
        } else {
            tasks = repository.findAll(sort);
            LOGGER.info("Retrieved all tasks sorted by " + sortBy + " in " + sortDirection + " order");
        }
        return listMapper.mapList(tasks, TaskDTO.class);
    }

    public TaskEntity getTaskById(int taskId) {
        return repository.findById(taskId).orElseThrow(() -> new ItemNotFoundException("Task " + taskId + " does not exist !!!"));
    }

    @Transactional
    public TaskEntity createTask(@Valid TaskRequest task) throws Throwable {
        TaskEntity taskEntity = modelMapper.map(task, TaskEntity.class);
        StatusEntity statusEntity = (StatusEntity) statusRepository.findById(task.getStatus()).orElseThrow(() -> new ItemNotFoundException("Task does not exist"));
        taskEntity.setStatus(statusEntity);

        return repository.save(taskEntity);
    }

    @Transactional
    public boolean updateTask(Integer id, @Valid TaskRequest editTask) throws Throwable {
        TaskEntity task = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task " + id + " does not exist !!!"));

        Integer statusId = (Integer) editTask.getStatus();
        StatusEntity statusEntity = (StatusEntity) statusRepository.findById(statusId).orElseThrow(() -> new ItemNotFoundException("Status " + statusId + " does not exist !!!"));

        task.setTitle(editTask.getTitle().trim());
        task.setDescription(editTask.getDescription() != null ? editTask.getDescription().trim() : null);
        task.setAssignees(editTask.getAssignees() != null ? editTask.getAssignees().trim() : null);
        task.setStatus(statusEntity);
        task.setUpdatedOn(ZonedDateTime.now().toOffsetDateTime());
        repository.save(task);
        return true;
    }

    @Transactional
    public boolean deleteTask(Integer id) {
        TaskEntity task = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task " + id + " does not exist !!!"));
        repository.delete(task);
        return true;
    }
}
