package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final MapperUtil mapperUtil;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TaskService taskService;

    public ProjectServiceImpl(MapperUtil mapperUtil, ProjectRepository projectRepository, UserService userService, TaskService taskService) {
        this.mapperUtil = mapperUtil;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {

        Project foundedProject = projectRepository.findByProjectCode(dto.getProjectCode());

        if (foundedProject != null) {
            throw new TicketingProjectException("Project with this code is already exists");
        }

        dto.setProjectStatus(Status.OPEN);

        Project obj = mapperUtil.convert(dto, new Project());
//        obj.setAssignedManager(userMapper.convertToEntity(dto.getAssignedManager()));

        Project createdProject = projectRepository.save(obj);

        return mapperUtil.convert(createdProject, new ProjectDTO());

    }

    @Override
    public void update(ProjectDTO dto) {

        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        Project convertedProject = mapperUtil.convert(dto, new Project());

        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());

        projectRepository.save(convertedProject);

    }

    @Override
    public void delete(String code) {

        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(mapperUtil.convert(project, new ProjectDTO()));

    }

    @Override
    public void complete(String projectCode) {

        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);

        projectRepository.save(project);

    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserDTO currentUserDTO = userService.findByUserName(username);
        User user = mapperUtil.convert(currentUserDTO, new User());
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project, new ProjectDTO());
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());

    }

}
