package com.project.controller;

import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.user.RegisterRequest;
import com.project.payload.request.user.ResetCodeRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.authentication.AuthResponse;


import com.project.payload.response.user.RegisterResponse;
import com.project.service.AuthenticationService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * F01 -> http://localhost:8080/users/login
     * !!! Kullanıcı giriş endpoint'i
     * POST isteği ile çalışır.
     * - Anonim kullanıcıların (henüz giriş yapmamış) erişimine izin verilir.
     * - Kullanıcı email ve şifre ile giriş yapar.
     * - Başarılı girişte, bir authentication token döner.
     */
    @PostMapping("/login") // http://localhost:8080/auth/login  + POST + JSON
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        return authenticationService.authenticateUser(loginRequest);
    }

    /**
     * F02 -> http://localhost:8080/users/register
     *
     * !!! Yeni bir kullanıcı kaydetmek için kullanılır.
     * POST isteği ile çalışır.
     * - Anonim kullanıcıların (henüz giriş yapmamış) erişimine izin verilir.
     *
     */
    @PostMapping("/register/{userRole}") // http://localhost:8080/users/register/Admin + POST + JSON
    public ResponseMessage<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest RegisterRequest){
        // Kullanıcıyı rolüne göre kaydeder.
        return authenticationService.registerUser(RegisterRequest);
    }


    @GetMapping("/logout")
    public String login() {
        return "logout successfully";
    }

    /**
     * F03 -> http://localhost:8080/users/logout
     * !!! Kullanıcı çıkış endpoint'i
     * - GET isteği ile çalışır.
     * - Kullanıcıyı sistemden başarıyla çıkış yaptırır.
     */
    @PostMapping("/forgot-password") //http://localhost:8080/auth/forgot-password
    public ResponseMessage<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        return authenticationService.forgotPassword(forgotPasswordRequest);
    }

    /**
     * F04 -> http://localhost:8080/auth/reset-password
     * !!! Kullanıcı şifresini sıfırlama endpoint'i
     * - POST isteği ile çalışır.
     * - Girilen şifre sıfırlama kodu ile yeni şifre belirlenir.
     * - Eğer girilen kod veritabanındaki kod ile eşleşiyorsa, şifre güncellenir.
     * - Eğer kod eşleşmezse, "kod geçersiz" hata mesajı döner.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String>resetPassword(@Valid @RequestBody ResetCodeRequest resetcoderequest){
        return authenticationService.resetPassword(resetcoderequest);
    }


}