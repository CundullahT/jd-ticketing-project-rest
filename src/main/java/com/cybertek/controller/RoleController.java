package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read All Roles")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all roles.", roleService.listAllRoles()));
    }

}
