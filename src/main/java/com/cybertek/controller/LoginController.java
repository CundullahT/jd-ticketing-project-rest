package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.annotation.ExecutionTime;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ConfirmationToken;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.entity.User;
import com.cybertek.entity.common.AuthenticationRequest;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.service.ConfirmationTokenService;
import com.cybertek.service.UserService;
import com.cybertek.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@Tag(name = "Authentication Controller", description = "Authenticate API")
public class LoginController {

    @Value("${app.local-url}")
    private String BASE_URL;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MapperUtil mapperUtil;
    private final JWTUtil jwtUtil;
    private final ConfirmationTokenService confirmationTokenService;

    public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil, ConfirmationTokenService confirmationTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.jwtUtil = jwtUtil;
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping("/authenticate")
    @DefaultExceptionMessage(defaultMessage = "Bad Credentials")
    @Operation(summary = "Login to application")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException, AccessDeniedException {

        String password = authenticationRequest.getPassword();
        String username = authenticationRequest.getUsername();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        UserDTO foundUser = userService.findByUserName(username);
        User convertedUser = mapperUtil.convert(foundUser, new User());

        if (!foundUser.isEnabled()) {
            throw new TicketingProjectException("Please verify your user");
        }

        String jwtToken = jwtUtil.generateToken(convertedUser);

        return ResponseEntity.ok(new ResponseWrapper("Login Successful", jwtToken));

    }

    @DefaultExceptionMessage(defaultMessage = "Failed to confirm email, please try again!")
    @GetMapping("/confirmation")
    @Operation(summary = "Confirm account")
    public ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketingProjectException {

        ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
        UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
        confirmationTokenService.delete(confirmationToken);

        return ResponseEntity.ok(new ResponseWrapper("User has been confirmed!", confirmUser));

    }

}
