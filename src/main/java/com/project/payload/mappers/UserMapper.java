package com.project.payload.mappers;

import com.project.entity.concretes.user.User;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userRole(user.getUserRole().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getRole()
                .name())
                .build();
    }

    public User mapUserRequestToUser(UserRequest userRequest) {
    }
}