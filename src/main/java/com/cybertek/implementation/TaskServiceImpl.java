package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private MapperUtil mapperUtil;


    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, MapperUtil mapperUtil) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public TaskDTO findById(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Task does not exists"));
        return mapperUtil.convert(task,new TaskDTO());
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> list = taskRepository.findAll();
        return list.stream().map(obj ->mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        Task task = mapperUtil.convert(dto,new Task());
        Task save = taskRepository.save(task);
        return mapperUtil.convert(save,new TaskDTO());
    }

    @Override
    public TaskDTO update(TaskDTO dto) throws TicketingProjectException {

        taskRepository.findById(dto.getId()).orElseThrow(() -> new TicketingProjectException("Task does not exists"));
        Task convertedTask = mapperUtil.convert(dto,new Task());
        Task save = taskRepository.save(convertedTask);
        return mapperUtil.convert(save,new TaskDTO());


    }

    @Override
    public void delete(long id) throws TicketingProjectException {
        Task foundTask = taskRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Task does not exists"));
        foundTask.setIsDeleted(true);
        taskRepository.save(foundTask);
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> taskDTOS = listAllByProject(project);
        taskDTOS.forEach(taskDTO -> {
            try {
                delete(taskDTO.getId());
            } catch (TicketingProjectException e) {
                e.printStackTrace();
            }
        });
    }


    public List<TaskDTO> listAllByProject(ProjectDTO project){
        List<Task> list = taskRepository.findAllByProject(mapperUtil.convert(project,new Project()));
        return list.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException {
        String id= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new TicketingProjectException("User does not exists"));
        List<Task> list= taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status,user);
        return list.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new TicketingProjectException("This user does not exist"));
        List<Task> tasks = taskRepository.findAllByProjectAssignedManager(user);
        return tasks.stream().map(obj ->mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateStatus(TaskDTO dto) throws TicketingProjectException {
        Task task = taskRepository.findById(dto.getId()).orElseThrow(() -> new TicketingProjectException("Task does not exists"));
        task.setTaskStatus(dto.getTaskStatus());
        Task save = taskRepository.save(task);
        return mapperUtil.convert(save,new TaskDTO());
    }

    @Override
    public List<TaskDTO> readAllByEmployee(User assignedEmployee) {
        List<Task> tasks = taskRepository.findAllByAssignedEmployee(assignedEmployee);
        return tasks.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    //    @Override
//    public List<TaskDTO> listAllTasksByStatus(Status status) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUserName(username);
//        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status,user);
//        return list.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
//    }
}