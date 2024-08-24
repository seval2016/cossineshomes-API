package com.project.payload.request.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractUserRequest {

    @NotNull(message = "Please enter your username")
    @Size(min = 4, max = 16, message = "Your username should be at least 4 chars" )
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your username must consist of the characters .")
    private String username;

    @NotNull(message = "Please enter your name")
    @Size(min = 4, max = 16, message = "Your name should be at least 4 chars" )
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your name must consist of the characters .")
    private String firstName;

    @NotNull(message = "Please enter your surname")
    @Size(min = 4, max = 16, message = "Your surname should be at least 4 chars" )
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your surname must consist of the characters .")
    private String lastName;

    @NotNull(message = "Please enter your phone number")
    @Size(min = 12, max = 12,message = "Your phone number should be 12 characters long")
    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    private String phone;

    @NotNull(message = "Please enter your email")
    @Email(message = "Please enter valid email")
    @Size(min=5, max=50 , message = "Your email should be between 5 and 50 chars")
    private String email;

}
