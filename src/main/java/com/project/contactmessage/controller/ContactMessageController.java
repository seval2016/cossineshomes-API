package com.project.contactmessage.controller;

import com.project.contactmessage.dto.ContactMessageRequest;
import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.contactmessage.entity.ContactMessage;
import com.project.contactmessage.service.ContactMessageService;
import com.project.payload.response.business.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactMessageController {
    private final ContactMessageService contactMessageService;

    // --> J01 - It will create a contact message
    @PostMapping("/contact-messages/create") //http://localhost:8080/contacts//contact-messages/create + POST + JSON
    public ResponseMessage<ContactMessageResponse> createMessage(@Valid @RequestBody ContactMessageRequest contactMessageRequest) {
        return contactMessageService.createMessage(contactMessageRequest);
    }

    //Pageable -> veriyi sıralayarak değil sayfalara bölerek gösterme teknolojisine denir.
    @GetMapping("/contact-messages/getAll") //http://localhost:8080/contacts/contact-messages/getAll + GET
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public Page<ContactMessageResponse> getAllContactMessages(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "status", defaultValue = "0") int status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "category_id") String sortField,
            @RequestParam(value = "type", defaultValue = "asc") String sortType) {
        return contactMessageService.getAllContactMessages(query, status, page, size, sortField, sortType);
    }

        //Bu, mesajın bir yönetici ya da yetkili tarafından okunduğunda durum değişikliği yapılması
        @GetMapping("/contact-messages/{id}")//http://localhost:8080/contacts/contact-messages/4  + GET
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
        public ResponseEntity<ContactMessage> getContactMessageById(@PathVariable Long id) {
            return ResponseEntity.ok(contactMessageService.findContactByIdAndUpdateStatus(id));
        }//J03

        @DeleteMapping("/delete/{id}")//http://localhost:8080/contacts/delete/2
        @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
        public ResponseEntity<String> deleteContactMessage(@PathVariable Long id) {
            String response = contactMessageService.deleteContactMessageById(id);
            return ResponseEntity.ok(response);
        }//J04
        
}