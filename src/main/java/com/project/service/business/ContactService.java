package com.project.service.business;


import com.project.entity.concretes.business.Contact;
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

    public Page<ContactResponse> getAllContactMessages(int page, int size, String sortField, String sortType) {
        // Sıralama yönünü belirlemek için sortType parametresini kullanıyoruz
        Sort.Direction direction = Sort.Direction.fromString(sortType);

        // Sayfa, boyut ve sıralama bilgileriyle pageable nesnesini oluşturuyoruz
        Pageable pageable = PageRequest.of(page,size, Sort.by(direction,sortField));

        return contactRepository.findAll(pageable).map(contactMapper::contactToResponse);
    }
}
