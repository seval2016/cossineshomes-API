package com.project.contactmessage.mapper;

import com.project.contactmessage.dto.ContactRequest;
import com.project.contactmessage.dto.ContactResponse;
import com.project.contactmessage.entity.ContactMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactMapper {

    //!!! Request DTO --> POJO

    public ContactMessage requestToContactMessage(ContactRequest contactRequest){
        return ContactMessage.builder()
                .firstName(contactRequest.getFirstName())
                .lastName(contactRequest.getLastName())
                .message(contactRequest.getMessage())
                .email(contactRequest.getEmail())
                .createAt(LocalDateTime.now())
                .build();
    }

    // !!! POJO --> Response DTO
    public ContactResponse contactMessageToResponse(ContactMessage contactMessage){

        return ContactResponse.builder()
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