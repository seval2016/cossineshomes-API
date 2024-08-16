package com.project.contactmessage.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class ContactMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactMessageId;

    @NotNull
    private String contactName;

    @NotNull
    private String contactEmail;

    @NotNull
    private String contactSubject;

    @NotNull
    private String contactMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "US")
    private LocalDateTime dateTime;

}