package com.project.service.business;


import com.project.contactmessage.messages.Messages;
import com.project.entity.concretes.business.Contact;
import com.project.entity.enums.ContactStatus;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.ContactMapper;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.ContactRequest;
import com.project.payload.response.business.ContactResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ResponseMessage<ContactResponse> create(ContactRequest contactRequest) {

        //Gelen request db ye kaydedilmesi için pojo ya çevriliyor
        Contact contactMessage=contactMapper.requestToContact(contactRequest);
        //Gelen mesaj db ye kaydediliyor
        Contact createdMessage=contactRepository.save(contactMessage);

        return ResponseMessage.<ContactResponse>builder()
                .message(SuccessMessages.CONTACT_MESSAGE_CREATED)
                .httpStatus(HttpStatus.CREATED)
                .object(contactMapper.contactToResponse(createdMessage))
                .build();
    }

    public Page<ContactResponse> getAllContactMessages(String query, int status, int page, int size, String sortField, String sortType) {
        // Sıralama yönünü belirlemek için sortType parametresini kullanıyoruz
        Sort.Direction direction = Sort.Direction.fromString(sortType);

        // Sayfa, boyut ve sıralama bilgileriyle pageable nesnesini oluşturuyoruz
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Hem query hem de status ile filtreleme yapıyoruz
        if (query == null || query.isEmpty()) {
            // Eğer query boşsa sadece status'a göre filtreleme yap
            return contactRepository.findByStatus(status, pageable).map(contactMapper::contactToResponse);
        }

        // Query ve status'a göre filtreleme yap
        return contactRepository.findByStatusAndMessageContaining(status, query, pageable).map(contactMapper::contactToResponse);
    }

    public Contact findContactByIdAndUpdateStatus(Long id) {
        //verilen id'nin var olup olmadığını kontrol eder
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));

        //Eğer id var ise status durumunu 1 olarak günceller
        contact.setStatus(ContactStatus.OPENED.getStatusValue());
        return contactRepository.save(contact);
    }

    public Contact markContactMessageAsDeleted(Long id) {
        // İlgili mesajı bul
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + id));

        // Mesajın status alanını 1 olarak güncelle (silindi olarak işaretle)
        contact.setStatus(ContactStatus.OPENED.getStatusValue());  // Status is set to 1 (opened)
        return contactRepository.save(contact); // Güncellenmiş mesajı veritabanına kaydet
    }

    public String deleteContactMessageById(Long id) {
        getContactMessageById(id);
        contactRepository.deleteById(id);
        return Messages.CONTACT_MESSAGE_DELETED_SUCCESSFULLY;
    }

    public Contact getContactMessageById(Long id){
        return contactRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_MESSAGE));
    }
}
