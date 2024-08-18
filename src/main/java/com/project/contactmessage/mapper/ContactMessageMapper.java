package com.project.contactmessage.mapper;

import com.project.contactmessage.dto.ContactMessageRequest;
import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.contactmessage.entity.ContactMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactMessageMapper {

    //!!! Request DTO --> POJO

    public ContactMessage requestToContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .contactName(contactMessageRequest.getName())
                .contactSubject(contactMessageRequest.getSubject())
                .contactMessage(contactMessageRequest.getMessage())
                .contactEmail(contactMessageRequest.getEmail())
                .dateTime(LocalDateTime.now())
                .build();
    }

    // !!! POJO --> Response DTO
    public ContactMessageResponse contactMessageToResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder()
                .name(contactMessage.getContactName())
                .subject(contactMessage.getContactSubject())
                .message(contactMessage.getContactMessage())
                .email(contactMessage.getContactEmail())
                .dateTime(LocalDateTime.now())
                .build();

    }

}