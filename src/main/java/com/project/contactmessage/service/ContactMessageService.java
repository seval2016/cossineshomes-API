package com.project.contactmessage.service;

import com.project.contactmessage.dto.ContactMessageRequest;
import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.contactmessage.entity.ContactMessage;
import com.project.contactmessage.entity.ContactStatus;
import com.project.contactmessage.mapper.ContactMessageMapper;
import com.project.contactmessage.messages.Messages;
import com.project.contactmessage.repository.ContactMessageRepository;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.response.business.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ResponseMessage<ContactMessageResponse> createMessage(ContactMessageRequest contactMessageRequest) {
        // DTO'yu POJO'ya dönüştür
        ContactMessage contactMessage = contactMessageMapper.requestToContactMessage(contactMessageRequest);

        // Veritabanına kaydet
        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        // Başarı mesajını oluştur ve döndür
        return ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact Message Created Successfully")
                .httpStatus(HttpStatus.CREATED) // 201
                .object(contactMessageMapper.contactMessageToResponse(savedData)) // POJO'dan DTO'ya dönüşüm
                .build();
    }

    public Page<ContactMessageResponse> getAllContactMessages(String query, int status, int page, int size, String sortField, String sortType) {
        // Sıralama yönünü belirlemek için sortType parametresini kullanıyoruz
        Sort.Direction direction = Sort.Direction.fromString(sortType);

        // Sayfa, boyut ve sıralama bilgileriyle pageable nesnesini oluşturuyoruz
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Hem query hem de status ile filtreleme yapıyoruz
        if (query == null || query.isEmpty()) {
            // Eğer query boşsa sadece status'a göre filtreleme yap
            return contactMessageRepository.findByStatus(status, pageable).map(contactMessageMapper::contactMessageToResponse);
        }

        // Query ve status'a göre filtreleme yap
        return contactMessageRepository.findByStatusAndMessageContaining(status, query, pageable).map(contactMessageMapper::contactMessageToResponse);
    }

    public String deleteContactMessageById(Long id) {
        getContactMessageById(id);
        contactMessageRepository.deleteById(id);
        return Messages.CONTACT_MESSAGE_DELETED_SUCCESSFULLY;
    }

    public ContactMessage getContactMessageById(Long id) {
        // İd ile mesajı bul, bulunamazsa hata fırlat
        return contactMessageRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_MESSAGE));
    }

    public ContactMessage findContactByIdAndUpdateStatus(Long id) {
        //verilen id'nin var olup olmadığını kontrol eder
        ContactMessage contact = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));

        //Eğer id var ise status durumunu 1 olarak günceller
        contact.setStatus(ContactStatus.OPENED.getStatusValue());
        return contactMessageRepository.save(contact);
    }
}
