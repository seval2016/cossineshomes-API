package com.project.payload.request.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AuthenticatedUsersRequest {

    @NotNull(message = "First name can not be a null")
    @Size(min = 2, max = 30, message = "First name '${validatedValue}' must be between {min} and {max}")
    private String firstName;
    @NotNull(message = "Last name can not be a null")
    @Size(min = 2, max = 30, message = "Last name '${validatedValue}' must be between {min} and {max}")
    private String lastName;
    @Email
    @NotNull(message = "Email can not be a null")
    private String email;
    @NotNull(message = "Phone number can not be a null")
    private String phone;




}