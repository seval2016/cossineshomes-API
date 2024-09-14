package com.project.controller;

import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.SuccessMessages;

import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.ResetCodeRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.UserResponse;
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

    @PostMapping("/login") // http://localhost:8080/auth/login  + POST + JSON
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        return authenticationService.authenticateUser(loginRequest);
    } //F01

    @GetMapping("/logout")
    public String login() {
        return "logout successfully";
    }

    @PostMapping("/forgot-password") //http://localhost:8080/auth/forgot-password
    public ResponseMessage<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        return userService.forgotPassword(forgotPasswordRequest);
    }//F03

    @PostMapping("/reset-password") //http://localhost:8080/auth/reset-password
    public ResponseEntity<String>resetPassword(@Valid @RequestBody ResetCodeRequest resetcoderequest){
        return userService.resetPassword(resetcoderequest);
    } //F04


}