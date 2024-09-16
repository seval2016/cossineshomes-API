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

    private final MethodHelper methodHelper;
    private final TourRequestMapper tourRequestMapper;
    private final AdvertMapper advertMapper;


    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .advert(user.getAdvert() != null ? user.getAdvert().stream().map(advertMapper::mapAdvertToAdvertResponse).collect(Collectors.toSet()) : new HashSet<>())
                .favoritesList(user.getFavoritesList() != null ? user.getFavoritesList().stream().map(Favorite::getId).collect(Collectors.toSet()) : new HashSet<>())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userRole(user.getUserRole() != null ? user.getUserRole().stream().map(UserRole::getRoleName).collect(Collectors.toSet()) : new HashSet<>())
                .tourRequestsResponse(user.getTourRequests() != null ? user.getTourRequests().stream().map(tourRequestMapper::mapTourRequestToTourRequestResponse).collect(Collectors.toSet()) : new HashSet<>())
                .builtIn(user.getBuiltIn())
                .build();
    }
    public User mapUserRequestToUser(BaseUserRequest userRequest) {

        return User.builder()
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phone(userRequest.getPhone())
                .builtIn(userRequest.getBuiltIn())
                .build();
    }
    public CustomerResponse mapUserToCustomerResponse(User customer) {
        return  CustomerResponse.builder()
                .userId(customer.getId())
                .username(customer.getUsername())
                .firstname(customer.getFirstName())
                .lastname(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }
    public User mapUserRequestToUpdatedUser(UserRequest userRequest, Long userId) {

        return User.builder()
                .id(userId)
                .username(userRequest.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .passwordHash(userRequest.getPasswordHash())
                .phone(userRequest.getPhone())
                .email(userRequest.getEmail())
                .build();

    }
    public void mapUserRequestWithoutPasswordToUser(UserRequestWithoutPassword userRequestWithoutPassword, User user) {
        user.setFirstName(userRequestWithoutPassword.getFirstName());
        user.setLastName(userRequestWithoutPassword.getLastName());
        user.setPhone(userRequestWithoutPassword.getPhone());
        user.setEmail(userRequestWithoutPassword.getEmail());
    }
    public User mapUserResponseToUser(BaseUserResponse authenticatedUser) {
        return User.builder()
                .id(authenticatedUser.getUserId())
                .username(authenticatedUser.getUsername())
                .firstName(authenticatedUser.getFirstname())
                .lastName(authenticatedUser.getLastname())
                .email(authenticatedUser.getEmail())
                .phone(authenticatedUser.getPhone())
                .userRole(authenticatedUser.getUserRole()
                        .stream()
                        .map(roleName -> {
                            UserRole userRole = new UserRole();
                            userRole.setRoleName(roleName);
                            userRole.setRole(RoleType.valueOf(roleName.toUpperCase()));
                            return userRole;
                        })
                        .collect(Collectors.toList()))
                .build();
    }
    public User userRequestToUser(UserSaveRequest request) {

        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .builtIn(request.getBuiltIn())
                .build();

    }
    public RegisterResponse userToRegisterResponse(User newRegisterUser){
        return RegisterResponse.builder()
                .id(newRegisterUser.getId())
                .firstName(newRegisterUser.getFirstName())
                .lastName(newRegisterUser.getLastName())
                .phone(newRegisterUser.getPhone())
                .email(newRegisterUser.getEmail())
                .build();
    }

    public CustomerResponse customerToCustomerResponse(User user) {
        return CustomerResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .advert(user.getAdvert().stream().map(advertMapper::mapAdvertToAdvertResponse).collect(Collectors.toSet()))
                .favoritesList(user.getFavoritesList().stream().map(Favorite::getId).collect(Collectors.toSet()))
                .tourRequestsResponse(user.getTourRequests().stream().map(tourRequestMapper::mapTourRequestToTourRequestResponse).collect(Collectors.toSet()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userRole(user.getUserRole().stream().map(UserRole::getRoleName).collect(Collectors.toSet()))
                .tourRequestsResponse(user.getTourRequests().stream().map(tourRequestMapper::mapTourRequestToTourRequestResponse).collect(Collectors.toSet()))
                .build();
    }

}