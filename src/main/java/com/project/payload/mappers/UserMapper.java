package com.project.payload.mappers;

import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.payload.request.abstracts.BaseUserRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.request.user.UserSaveRequest;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.user.CustomerResponse;
import com.project.payload.response.UserResponse;

import com.project.payload.response.user.RegisterResponse;
import com.project.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .build();
    }

    public User mapUserRequestToUser(BaseUserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .passwordHash(userRequest.getPasswordHash())
                .build();
    }

    public CustomerResponse mapUserToCustomerResponse(User customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .build();
    }

    public User mapUserRequestToUpdatedUser(UserRequest userRequest, Long userId) {
        return User.builder()
                .id(userId)
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .build();
    }

    public void mapUserRequestWithoutPasswordToUser(UserRequestWithoutPassword userRequestWithoutPassword, User user) {
        user.setFirstName(userRequestWithoutPassword.getFirstName());
        user.setLastName(userRequestWithoutPassword.getLastName());
        user.setEmail(userRequestWithoutPassword.getEmail());
        user.setPhone(userRequestWithoutPassword.getPhone());
    }

    public User mapUserResponseToUser(BaseUserResponse authenticatedUser) {
        return User.builder()
                .id(authenticatedUser.getId())
                .firstName(authenticatedUser.getFirstName())
                .lastName(authenticatedUser.getLastName())
                .email(authenticatedUser.getEmail())
                .phone(authenticatedUser.getPhone())
                .build();
    }

    public User userRequestToUser(UserSaveRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(request.getPasswordHash())
                .build();
    }

    public RegisterResponse userToRegisterResponse(User newRegisterUser) {
        return RegisterResponse.builder()
                .id(newRegisterUser.getId())
                .firstName(newRegisterUser.getFirstName())
                .lastName(newRegisterUser.getLastName())
                .build();
    }

    public CustomerResponse customerToCustomerResponse(User user) {
        return CustomerResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}