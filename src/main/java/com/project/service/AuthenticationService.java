package com.project.service;

import com.project.entity.concretes.user.User;
import com.project.exception.BadRequestException;
import com.project.exception.MailServiceException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;

import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.business.UpdatePasswordRequest;

import com.project.payload.response.UserResponse;

import com.project.payload.response.authentication.AuthResponse;
import com.project.repository.user.UserRepository;
import com.project.security.jwt.JwtUtils;
import com.project.security.service.UserDetailsImpl;
import com.project.service.email.EmailService;
import com.project.service.email.EmailServiceInterface;
import com.project.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailServiceInterface emailServiceInterface;


    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        String username=loginRequest.getUsername();

        String password=loginRequest.getPassword();

        //!!! authenticationManager uzerinden kullanici valide ediliyor

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        //!!! valide edilen kullanici context e atiliyor
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //!!! JWT token olusturuluyor
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        //!!! Response nesnesi olusturuluyor
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //!!! grantedAuth --> Role ( String )
        Set<String> roles = userDetails.getAuthorities()
                .stream() // Sream<GrantedAuth>
                .map(GrantedAuthority::getAuthority) // Stream<String>
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.id(userDetails.getId());
        authResponse.builtIn(userDetails.getBuiltIn());
        authResponse.email(userDetails.getEmail());
        authResponse.token(token.substring(7));
        authResponse.firstName(userDetails.getFirstName());
        authResponse.lastName(userDetails.getLastName());
        authResponse.userRole(roles);
        authResponse.phone(userDetails.getPhone());

        try {
            MimeMessagePreparator registrationEmail = MailUtil.buildRegistrationEmail(userDetails.getEmail() , userDetails.getFirstName());
            emailServiceInterface.sendEmail(registrationEmail);
        } catch (Exception e) {
            throw new MailServiceException(e.getMessage());
        }
        return ResponseEntity.ok(authResponse.build());

    }

}
