package com.project.service;

import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.MailServiceException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;

import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.authentication.LoginRequest;
import com.project.payload.request.business.ForgotPasswordRequest;

import com.project.payload.request.user.RegisterRequest;
import com.project.payload.request.user.ResetCodeRequest;

import com.project.payload.response.ResponseMessage;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.authentication.AuthResponse;
import com.project.payload.response.user.RegisterResponse;
import com.project.repository.user.UserRepository;
import com.project.security.jwt.JwtUtils;
import com.project.security.service.UserDetailsImpl;
import com.project.service.email.EmailService;
import com.project.service.email.EmailServiceInterface;
import com.project.service.helper.MethodHelper;
import com.project.service.user.UserRoleService;
import com.project.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final EmailServiceInterface emailServiceInterface;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final MethodHelper methodHelper;
    private final EmailService emailService;

    /**
     * Bu metod, halihazırda oturum açmış olan kullanıcının
     * bilgilerini HttpServletRequest'ten alır ve kullanıcıyı
     * BaseUserResponse objesine dönüştürüp geri döner.
     *
     * @param request HTTP isteği ile gelen kullanıcı bilgilerini içerir.
     * @return BaseUserResponse doğrulanmış kullanıcının bilgilerini döner.
     */
    public BaseUserResponse getCurrentAuthenticatedUser(HttpServletRequest request) {
        User user = userRepository.findByUsernameEquals((String) request.getAttribute("email"));
        return userMapper.mapUserToUserResponse(user);
    }
    /**
     * F01 - Bu metod, kullanıcının giriş yapmasını sağlar.
     *
     * Kullanıcı kimlik bilgilerini doğrular, başarılı giriş
     * sonrası JWT token oluşturur ve AuthResponse ile kullanıcı bilgilerini döner.
     *
     * @param loginRequest Kullanıcı adı ve şifre bilgilerini içeren istek.
     * @return AuthResponse doğrulama sonrası oluşturulan JWT token ve kullanıcı bilgilerini döner.
     */
    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername(); // Kullanıcıdan gelen kullanıcı adı
        String password = loginRequest.getPassword(); // Kullanıcıdan gelen şifre

        //!!! authenticationManager üzerinden kullanici valide ediliyor
        // Kullanıcının girdiği kimlik bilgileri doğrulanıyor (kullanıcı adı ve şifre)
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        //!!! valide edilen kullanıcı context'e atılıyor
        // Doğrulanan kullanıcıyı security context'e yerleştiriyoruz, bu kullanıcı artık yetkili sayılır.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //!!! JWT token oluşturuluyor
        // Kullanıcıya ait bir JWT token oluşturuluyor, bu token kimlik doğrulamada kullanılacak
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        //!!! Response nesnesi oluşturuluyor
        // Kullanıcıya geri dönecek olan AuthResponse nesnesi oluşturuluyor.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //!!! grantedAuth --> Role ( String )
        // Kullanıcının rollerini alıp, String olarak bir Set içerisine topluyoruz.
        Set<String> roles = userDetails.getAuthorities()
                .stream() // Stream<GrantedAuthority>
                .map(GrantedAuthority::getAuthority) // Stream<String>
                .collect(Collectors.toSet());

        //!!! Kullanıcının ilk rolünü almak için bir Optional kullanılıyor.
        Optional<String> role = roles.stream().findFirst();

        //!!! AuthResponse nesnesi inşa ediliyor
        // AuthResponse nesnesi builder deseni ile oluşturuluyor, gerekli alanlar ekleniyor.
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.id(userDetails.getId());
        authResponse.builtIn(userDetails.getBuiltIn());
        authResponse.email(userDetails.getEmail());
        authResponse.token(token.substring(7)); // Token'dan "Bearer" kısmı çıkarılıyor.
        authResponse.firstName(userDetails.getFirstName());
        authResponse.lastName(userDetails.getLastName());
        authResponse.userRole(roles); // Kullanıcının rolleri ekleniyor
        authResponse.phone(userDetails.getPhone());

        //!!! Kullanıcıya kayıt sonrası e-posta gönderimi yapılıyor
        try {
            MimeMessagePreparator registrationEmail = MailUtil.buildRegistrationEmail(userDetails.getEmail(), userDetails.getFirstName());
            emailServiceInterface.sendEmail(registrationEmail);
        } catch (Exception e) {
            //!!! Eğer e-posta gönderiminde bir hata oluşursa MailServiceException fırlatılıyor
            throw new MailServiceException(e.getMessage());
        }

        //!!! Response geri döndürülüyor
        // Kullanıcıya oluşturulan AuthResponse nesnesi JSON formatında geri gönderiliyor.
        return ResponseEntity.ok(authResponse.build());
    }


    public ResponseMessage<RegisterResponse> registerUser(RegisterRequest registerRequest) {

        // Duplicate email & phone kontrolü
        methodHelper.checkDuplicate(registerRequest.getEmail(), registerRequest.getPhone());

        User newRegisterUser = new User();
        newRegisterUser.setFirstName(registerRequest.getFirstName());
        newRegisterUser.setLastName(registerRequest.getLastName());
        newRegisterUser.setEmail(registerRequest.getEmail());
        newRegisterUser.setPhone(registerRequest.getPhone());
        newRegisterUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPasswordHash()));

        List<UserRole> roles = new ArrayList<>();

        // Rol kontrolü
        if (registerRequest.getRole() == null || registerRequest.getRole().isEmpty()) {
            UserRole defaultRole = userRoleService.getUserRole(RoleType.CUSTOMER);
            if (defaultRole != null) {
                roles.add(defaultRole);
            }
        } else {
            for (RoleType roleType : registerRequest.getRole()) {
                UserRole userRole = userRoleService.getUserRole(roleType);
                if (userRole != null) {
                    roles.add(userRole);
                }
            }
        }

        newRegisterUser.setUserRole(roles);

        // Kullanıcıyı kaydetme
        User registeredUser = userRepository.save(newRegisterUser);

        // Email gönderme
        try {
            MimeMessagePreparator registrationEmail = MailUtil.buildRegistrationEmail(registeredUser.getEmail(), registeredUser.getFirstName());
            emailService.sendEmail(registrationEmail);
        } catch (Exception e) {
            throw new MailServiceException(e.getMessage());
        }

        return ResponseMessage.<RegisterResponse>builder()
                .message(SuccessMessages.USER_CREATED)
                .object(userMapper.userToRegisterResponse(registeredUser))
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public ResponseMessage<String> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String resetCode;
        try {
            User user = methodHelper.findByUserByEmail(forgotPasswordRequest.getEmail());
            resetCode = UUID.randomUUID().toString();
            user.setResetPasswordCode(resetCode);
            userRepository.save(user);

            MimeMessagePreparator resetPasswordEmail = MailUtil.buildResetPasswordEmail(user.getEmail(), resetCode, user.getFirstName());
            emailService.sendEmail(resetPasswordEmail);
        } catch (BadRequestException e) {
            throw new ResourceNotFoundException("User", "email", forgotPasswordRequest.getEmail());
        }

        return ResponseMessage.<String>builder()
                .message("Code has been sent")
                .httpStatus(HttpStatus.OK)
                .object("Password reset code has been sent to your email")
                .build();
    }

    public ResponseEntity<String> resetPassword(ResetCodeRequest resetcodeRequest) {
        User user = userRepository.findByResetPasswordCode(resetcodeRequest.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid code"));

        user.setPasswordHash(passwordEncoder.encode(resetcodeRequest.getPassword()));
        user.setResetPasswordCode(null);
        userRepository.save(user);

        return ResponseEntity.ok(SuccessMessages.USER_UPDATED);
    }
}
