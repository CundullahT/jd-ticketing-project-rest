package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final MapperUtil mapperUtil;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, @Lazy ProjectService projectService, TaskService taskService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserName(username);
        return mapperUtil.convert(user, new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketingProjectException {

        User foundUser = userRepository.findByUserName(dto.getUserName());

        if (foundUser != null) {
            throw new TicketingProjectException("User already exists");
        }

        User user = mapperUtil.convert(dto, new User());
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));

        User save = userRepository.save(user);

        return mapperUtil.convert(save, new UserDTO());

    }

    @Override
    public UserDTO update(UserDTO dto) throws TicketingProjectException {

        //  Find current user
        User user = userRepository.findByUserName(dto.getUserName());

        if (user == null) {
            throw new TicketingProjectException("User Does Not Exists");
        }

        //  Map update user dto to entity object
        User convertedUser = mapperUtil.convert(dto, new User());

        convertedUser.setPassWord(passwordEncoder.encode(convertedUser.getPassWord()));
        convertedUser.setEnabled(true);

        //  Set id to the converted object
        convertedUser.setId(user.getId());

        //  Save updated user
        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());

    }

    @Override
    public void delete(String username) throws TicketingProjectException {

        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new TicketingProjectException("User Does Not Exists");
        }

        if (!checkIfUserCanBeDeleted(user)) {
            throw new TicketingProjectException("User can not be deleted. It is linked by a project ot task");
        }

        user.setUserName(user.getUserName() + "-" + user.getId());

        user.setIsDeleted(true);
        userRepository.save(user);

    }

    //  Hard Delete
    @Override
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }


    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {
            return mapperUtil.convert(obj, new UserDTO());
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectList = projectService.readAllByAssignedManager(user);
                return projectList.size() == 0;
            case "Employee":
                List<TaskDTO> taskList = taskService.readAllByEmployee(user);
                return taskList.size() == 0;
            default:
                return true;
        }

    }

    @Override
    public UserDTO confirm(User user) {

        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);

        return mapperUtil.convert(confirmedUser, new UserDTO());

    }

}
