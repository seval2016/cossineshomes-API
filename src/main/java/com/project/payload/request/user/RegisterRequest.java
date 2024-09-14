package com.project.payload.request.user;

import com.project.entity.enums.RoleType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 30, message = "First name '${validatedValue}' must be between {min} and {max}")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$", message = "Your first name must be consist of the characters a-z")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30, message = "Last name '${validatedValue}' must be between {min} and {max} long")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$", message = "Your last name must be consist of the characters a-z")
    private String lastName;

    @Size(min = 10, max = 12, message = "Your phone number should be at least 12 chars")
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
            message = "Please enter valid phone number")
    @NotBlank(message = "Please enter your phone number")
    private String phone;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter valid email")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
    @Size(min = 10, max = 80, message = "Your email '${validatedValue}' should be between {min} and {max} chars")
    private String email;

    @NotBlank(message = "Enter a valid Password")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase character, one uppercase character, and one special character (@#$%^&+=)")
    private String passwordHash;

    private Set<RoleType> role;
}
