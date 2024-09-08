package com.project.controller.business;

import com.project.payload.request.business.ContactRequest;
import com.project.payload.response.business.ContactResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

   private final ContactService contactService;

    @PostMapping("/create/contact-messages") // http://localhost:8080/contacts/create/contact-messages + POST + JSON
    @PreAuthorize("permitAll()")
    public ResponseMessage<ContactResponse> createContactMessage(@Valid @RequestBody ContactRequest contactRequest){
        return  contactService.create(contactRequest);
    }//J01

    @GetMapping("/getContactMessages") // http://localhost:8080/contacts/getContactMessages + GET
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Page<ContactResponse> getAllContactMessages(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "status", defaultValue = "0") int status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "category_id") String sortField,
            @RequestParam(value = "type", defaultValue = "asc") String sortType) {
        return contactService.getAllContactMessages(query, status, page, size, sortField, sortType);
    }//J02
}
