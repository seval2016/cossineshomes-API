package com.project.payload.response.abstracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long userId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String userRole;
}
