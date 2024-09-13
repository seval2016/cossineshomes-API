package com.project.contactmessage.mapper;

import com.project.contactmessage.dto.ContactMessageRequest;
import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.contactmessage.entity.ContactMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactMessageMapper {

    //!!! Request DTO --> POJO

    public ContactMessage requestToContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .firstName(contactMessageRequest.getFirstName())
                .lastName(contactMessageRequest.getLastName())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .createAt(LocalDateTime.now())
                .build();
    }

    // !!! POJO --> Response DTO
    public ContactMessageResponse contactMessageToResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder()
                .firstName(contactMessage.getFirstName())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

    }

    // !!! Pageable oluşturma metodu
    public Pageable createPageable(int page, int size, String sort, String type) {
        Sort.Direction direction = Sort.Direction.fromString(type);
        return PageRequest.of(page, size, Sort.by(direction, sort));
    }
}