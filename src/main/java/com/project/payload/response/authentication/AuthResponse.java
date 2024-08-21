package com.project.payload.response.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String username;
    private String role;
    private String token;
    private String name;
}
