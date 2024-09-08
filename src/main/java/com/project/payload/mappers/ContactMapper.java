package com.project.payload.mappers;

import com.project.entity.concretes.business.Contact;
import com.project.payload.request.business.ContactRequest;
import com.project.payload.response.business.ContactResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactMapper {

    // !!! Request --> POJO
    public Contact requestToContact(ContactRequest contactRequest){
        return Contact.builder()
                .firstName(contactRequest.getName())
                .email(contactRequest.getEmail())
                .message(contactRequest.getMessage())
                .createAt(LocalDateTime.now())
                .build();
    }

    // !!! pojo --> Response
    public ContactResponse contactToResponse(Contact contact){

        return ContactResponse.builder()
                .name(contact.getFirstName())
                .message(contact.getMessage())
                .email(contact.getEmail())
                .dateTime(LocalDateTime.now())
                .build();

    }
}
