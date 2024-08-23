package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.Role;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.UserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.user.UserRepository;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;


    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! Girilen username,email ve phone
        uniquePropertyValidator.checkDuplicate(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getPhone());

        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest);

        //!!! Rol bilgisini setliyoruz
        if(userRole.equalsIgnoreCase(Role.ADMIN.name())){
            //!!! Rol bilgisi admin ise builtin true yapılıyor
            if(Objects.equals(userRequest.getUsername(),"Admin")){
                user.setBuiltIn(true);
            }
            //!!! admin rolu veriliyor
            user.setUserRole(userRoleService.getAllUserRole(Role.ADMIN));
        } else if (userRole.equalsIgnoreCase("Manager")) {
            user.setUserRole(userRoleService.getAllUserRole(Role.MANAGER));
        } else {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USERROLE_MESSAGE, userRole));
        }
        //!!! password encode
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_CREATED)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {
    }
}
