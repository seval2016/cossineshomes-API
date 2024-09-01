package com.project.controller;

import com.project.entity.concretes.user.User;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.SuccessMessages;

import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.authentication.AuthResponse;


import com.project.service.AuthenticationService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/login") // http://localhost:8080/auth/login  + POST + JSON
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){

        return authenticationService.authenticateUser(loginRequest);
    }

    @GetMapping("/user") // http://localhost:8080/auth/user + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<UserResponse> findByUsername(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        UserResponse userResponse =  authenticationService.findByUsername(username);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/reset-password")  // http://localhost:8080/auth/resetPassword + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
                                                 HttpServletRequest request){
        authenticationService.updatePassword(updatePasswordRequest, request);
        String response = SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE;
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(SuccessMessages.PASSWORD_RESET_INSTRUCTIONS_SENT);
    }

    @GetMapping("/me") // http://www.cossineshomes.com/auth/me + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<UserResponse> getAuthenticatedUser(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        UserResponse userResponse = authenticationService.getAuthenticatedUser(username);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update/me") // http://www.cossineshomes.com/auth/update/me + PUT
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(@RequestBody @Valid UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        UserResponse updatedUserResponse = authenticationService.updateAuthenticatedUser(userRequestWithoutPassword, username);
        return ResponseEntity.ok(updatedUserResponse);
    }

    @PatchMapping("/updatePassword/me") // http://www.cossineshomes.com/auth/updatePassword/me + PATCH
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateAuthenticatedUserPassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        authenticationService.updateAuthenticatedUserPassword(updatePasswordRequest, username);
        return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE);
    }
}