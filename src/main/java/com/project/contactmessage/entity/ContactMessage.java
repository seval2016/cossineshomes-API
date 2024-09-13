package com.project.contactmessage.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class ContactMessage{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Size(max = 30 , message = "Your name must be a maximum of 30 characters.")
    private String firstName;

    @NotNull
    @Size(max = 30 , message = "Your lastname must be a maximum of 30 characters.")
    private String lastName;

    @NotNull
    @Size(max = 60 , message = "Your email must be a maximum of 60 characters.")
    @Email
    private String email;

    @NotNull
    @Size(max = 300 , message = "Your message must be a maximum of 300 characters.")
    private String message;

    @NotNull
    private int status=0;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH-mm", timezone = "Turkey")
    private LocalDateTime createAt;


}