package com.project.contactmessage.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest {

    //Client'dan gelmesi gereken bilgiler. Bu kısıamda validation yapılmalı

    @NotNull(message = "Please enter name")
    @Size(min = 2, max = 30, message = "Your name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String firstName;

    @NotNull(message = "Please enter your lastname")
    @Size(min = 2, max = 30 , message = "Your lastname must be a maximum of 30 characters.")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your lastname must be consist of character")
    private String lastName;

    @Email(message = "Please enter valid email")
    @NotNull(message = "Please enter your email")
    @Size(min = 3, max = 60, message = "Your email should be at least 3 chars")
    private String email;

    @NotNull(message = "Please enter message")
    @Size(min = 2, max = 300, message = "Your message should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String message ;

}