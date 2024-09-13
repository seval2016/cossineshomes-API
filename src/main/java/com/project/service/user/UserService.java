package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final MethodHelper methodHelper;

    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! Girilen username,email ve phone
        uniquePropertyValidator.checkDuplicate(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getPhone());

        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest);

        //!!! Rol bilgisini setliyoruz
        if(userRole.equalsIgnoreCase(RoleType.ADMIN.name())){
            //!!! Rol bilgisi admin ise builtin true yapılıyor
            if(Objects.equals(userRequest.getUsername(),"Admin")){
                user.setBuiltIn(true);
            }
            //!!! admin rolu veriliyor
            user.setUserRole(List.of(userRoleService.getUserRole(RoleType.ADMIN)));
        } else if (userRole.equalsIgnoreCase("Manager")) {
            user.setUserRole(List.of(userRoleService.getUserRole(RoleType.MANAGER)));
        } else {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USER_ROLE_MESSAGE,userRole));
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
        Pageable pageable=  pageableHelper.getPageableWithProperties(page, size, sort, type);

        return userRepository.findByUserByRole(userRole, pageable)
                .map(userMapper::mapUserToUserResponse);
    }

    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {

        BaseUserResponse baseUserResponse = null;

        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        if(user.getUserRole().stream()
                .anyMatch(role -> role.getRole() == RoleType.CUSTOMER)){
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

        //!!! silinecek olan user var mi ? kontrolu
        User user = methodHelper.isUserExist(id);

        //!!! metodu tetikleyen kullanicinin ad bilgisini aliyoruz
        String userName = (String) request.getAttribute("username");
        User user2 = userRepository.findByUsernameEquals(userName);

        //!!! builtIn ve Role kontrolu
        if(Boolean.TRUE.equals(user.isBuiltIn())){
            throw  new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);

        } else if (user2.getUserRole().stream()
                .anyMatch(role -> role.getRole() == RoleType.MANAGER)) {
            if(!(user.getUserRole().stream()
                    .anyMatch(role -> role.getRole() == RoleType.CUSTOMER))){
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        }
        userRepository.deleteById(id);
        return SuccessMessages.USER_DELETED;
    }

    public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {

        //!!! id var mi kontrolu :
        User user = methodHelper.isUserExist(userId);

        //!!! built_IN kontrolu
        methodHelper.checkBuiltIn(user);

        //!!! unique kontrolu :
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);

        //!!! DTO --> POJO
        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);

        //!!! password Hashlenecek
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

        //!!! builtIn
       methodHelper.checkBuiltIn(user);

        // unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, userRequestWithoutPassword);

        //!!! DTO --> POJO
        user.setUsername(userRequestWithoutPassword.getUsername());
        user.setFirstName(userRequestWithoutPassword.getFirstName());
        user.setLastName(userRequestWithoutPassword.getLastName());
        user.setEmail(userRequestWithoutPassword.getEmail());
        user.setPhone(userRequestWithoutPassword.getPhone());

        userRepository.save(user);

        String message = SuccessMessages.USER_UPDATED;
        return ResponseEntity.ok(message);
    }

    public List<UserResponse> getUserByName(String name) {

        return userRepository.getUserByFirstNameContaining(name) // List<User>
                .stream() // stream<User>
                .map(userMapper::mapUserToUserResponse) // stream<UserResponse>
                .collect(Collectors.toList()); // List<UserResponse>
    }

    //!!! Runner tarafi icin yazildi
    public long countAllAdmins(){
        return userRepository.countAdmin(RoleType.ADMIN);
    }

    public User getCustomerByUsername(String customerUsername){
        return userRepository.findByUsername(customerUsername);
    }

    public User getUserByUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE));
    }

    public List<User> getCustomerById(Long[] customerIds) {
        return userRepository.findByIdsEquals(customerIds);
    }


    public BaseUserResponse getAuthenticatedUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(username);
        return userMapper.mapUserToUserResponse(user);
    }

    public void updateAuthenticatedUserPassword(UpdatePasswordRequest passwordUpdateRequest, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(username);
        user.setPasswordHash(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    public List<User> getUsersByRoleType(RoleType roleType) {


        return userRepository.findByUserRole_RoleType(roleType);

    }
}
