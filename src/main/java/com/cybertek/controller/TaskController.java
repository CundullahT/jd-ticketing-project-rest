package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read All Tasks")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all tasks.", taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read All Tasks By Project Manager")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager() throws TicketingProjectException {
        List<TaskDTO> taskList = taskService.listAllTasksByProjectManager();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved tasks by project manager.", taskList));
    }

    @GetMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read Task By Id")
    @PreAuthorize("hasAnyAuthority('Manager', 'Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable("id") Long id) throws TicketingProjectException {
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved task.", taskService.findById(id)));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Create A New Task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> create(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(new ResponseWrapper("Successfully created task.", taskService.save(taskDTO)));
    }

    @DeleteMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Delete A Task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable("id") Long id) throws TicketingProjectException {
        taskService.delete(id);
        return ResponseEntity.ok(new ResponseWrapper("Successfully deleted."));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Update A Task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) throws TicketingProjectException {
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated task.", taskService.update(taskDTO)));
    }

    @GetMapping("/employee")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read All Non Complete Tasks")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeReadAllNonCompletedTasks() throws TicketingProjectException {
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved non completed tasks.", taskService.listAllTasksByStatusIsNot(Status.COMPLETE)));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeUpdateTask(@RequestBody TaskDTO taskDTO) throws TicketingProjectException {
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated task.", taskService.updateStatus(taskDTO)));
    }

}
