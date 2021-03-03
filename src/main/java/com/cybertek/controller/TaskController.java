package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read All Tasks")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all tasks.", taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong,please try again!")
    @Operation(summary = "Read all tasks by project manager")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager() throws TicketingProjectException {
        List<TaskDTO> taskList = taskService.listAllTasksByProjectManager();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved tasks by project manager", taskList));
    }

    @GetMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong,please try again!")
    @Operation(summary = "Read task by id")
    @PreAuthorize("hasAnyAuthority('Manager','Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable("id") Long id) throws TicketingProjectException {
        TaskDTO currentTask = taskService.findById(id);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved task", currentTask));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong,please try again!")
    @Operation(summary = "Create a new task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> create(@RequestBody TaskDTO task) {
        TaskDTO createdTask = taskService.save(task);
        return ResponseEntity.ok(new ResponseWrapper("Successfully task created", createdTask));
    }

    @DeleteMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong,please try again!")
    @Operation(summary = "Delete a task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable("id") Long id) throws TicketingProjectException {
        taskService.delete(id);
        return ResponseEntity.ok(new ResponseWrapper("Successfully deleted"));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong,please try again!")
    @Operation(summary = "Update task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO task) throws TicketingProjectException {
        TaskDTO updatedTask = taskService.update(task);
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated", updatedTask));
    }

    @GetMapping("/employee")
    @Operation(summary = "Read all non complete tasks")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeReadAllNonCompleteTask() throws TicketingProjectException {
        List<TaskDTO> tasks = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        return ResponseEntity.ok(new ResponseWrapper("Successfully read non completed current user tasks", tasks));
    }

    @PutMapping("/employee/update")
    @Operation(summary = "Read employee task")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeUpdateTask(@RequestBody TaskDTO taskDTO) throws TicketingProjectException {
        TaskDTO task = taskService.updateStatus(taskDTO);
        return ResponseEntity.ok(new ResponseWrapper("Successfully employee task status updated", task));
    }

}
