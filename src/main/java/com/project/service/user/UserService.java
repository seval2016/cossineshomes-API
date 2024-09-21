package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.MailServiceException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.*;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.RegisterResponse;
import com.project.repository.user.UserRepository;
import com.project.service.email.EmailService;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import com.project.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MethodHelper methodHelper;
    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final EmailService emailService;

    public BaseUserResponse getAuthenticatedUser(HttpServletRequest request) {
        User user = userRepository.findByUsernameEquals((String) request.getAttribute("email"));
        return userMapper.mapUserToUserResponse(user);
    } // F01

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

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findByUserByRole(userRole, pageable)
                .map(userMapper::mapUserToUserResponse);
    }

    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        BaseUserResponse baseUserResponse;
        if (user.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.CUSTOMER)) {
            baseUserResponse = userMapper.mapUserToCustomerResponse(user);
        } else {
            baseUserResponse = userMapper.mapUserToUserResponse(user);
        }

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(baseUserResponse)
                .build();
    }

    public String deleteUserById(Long id, HttpServletRequest request) {
        User user = methodHelper.isUserExist(id);
        String userName = (String) request.getAttribute("username");
        User user2 = userRepository.findByUsernameEquals(userName);

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        } else if (user2.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.MANAGER)) {
            if (!user.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.CUSTOMER)) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        }

        userRepository.deleteById(id);
        return SuccessMessages.USER_DELETED;
    }

    public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {
        User user = methodHelper.isUserExist(userId);
        methodHelper.checkBuiltIn(user);
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);

        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);
        updatedUser.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));
        updatedUser.setUserRole(user.getUserRole());
        User savedUser = userRepository.save(updatedUser);

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public ResponseEntity<String> updateAuthenticatedUser(UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        methodHelper.checkBuiltIn(user);
        uniquePropertyValidator.checkUniqueProperties(user, userRequestWithoutPassword);

        //user.setUsername(userRequestWithoutPassword);
        user.setFirstName(userRequestWithoutPassword.getFirstName());
        user.setLastName(userRequestWithoutPassword.getLastName());
        user.setEmail(userRequestWithoutPassword.getEmail());
        user.setPhone(userRequestWithoutPassword.getPhone());

        userRepository.save(user);

        return ResponseEntity.ok(SuccessMessages.USER_UPDATED);
    }

    public List<UserResponse> getUserByName(String name) {
        return userRepository.getUserByFirstNameContaining(name)
                .stream()
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());
    }

    public long countAllAdmins() {
        return userRepository.countAdmin(RoleType.ADMIN);
    }

    public User getCustomerByUsername(String customerUsername) {
        return userRepository.findByUsername(customerUsername);
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE));
    }

    public List<User> getCustomerById(Long[] customerIds) {
        return userRepository.findByIdsEquals(customerIds);
    }

    public void updateAuthenticatedUserPassword(UpdatePasswordRequest passwordUpdateRequest, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(username);
        user.setPasswordHash(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    public List<User> getUsersByRoleType(RoleType roleType) {
        return userRepository.findByUserRole_Role(roleType);
    }

    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return userMapper.mapUserToUserResponse(user);
    }

    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsername(userName);

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        }

        user.setPasswordHash(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public ResponseEntity<UserResponse> saveUserWithoutRequest(UserSaveRequest request) {
        methodHelper.checkDuplicate(request.getEmail(), request.getPhone());
        User savedUser = userMapper.userRequestToUser(request);
        savedUser.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));

        List<UserRole> userRolesSaved = new ArrayList<>();
        userRolesSaved.add(userRoleService.getUserRole(RoleType.ADMIN));
        savedUser.setUserRole(userRolesSaved);

        userRepository.save(savedUser);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
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
