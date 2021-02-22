package com.cybertek.service;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);

    List<ProjectDTO> listAllProjects();

    ProjectDTO save(ProjectDTO dto) throws TicketingProjectException;

    ProjectDTO update(ProjectDTO dto) throws TicketingProjectException;

    void delete(String code) throws TicketingProjectException;

    ProjectDTO complete(String projectCode) throws TicketingProjectException;

    List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException;

    List<ProjectDTO> readAllByAssignedManager(User user);

    List<ProjectDTO> listAllNonCompletedProjects();

}
