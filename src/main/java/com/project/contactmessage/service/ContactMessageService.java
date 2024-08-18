package com.project.contactmessage.service;

import com.project.contactmessage.dto.ContactMessageRequest;
import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.contactmessage.entity.ContactMessage;
import com.project.contactmessage.mapper.ContactMessageMapper;
import com.project.contactmessage.messages.Messages;
import com.project.contactmessage.repository.ContactMessageRepository;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.response.business.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {
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

    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {
        // Pageable nesnesini oluştur
        Pageable pageable = contactMessageMapper.createPageable(page, size, sort, type);

        // Veritabanından verileri al ve dönüşüm yap
        return contactMessageRepository.findAll(pageable)
                .map(contactMessageMapper::contactMessageToResponse);
    }

    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {
        // Pageable nesnesini oluştur
        Pageable pageable = contactMessageMapper.createPageable(page, size, sort, type);

        // Email'e göre arama yap ve dönüşüm yap
        return contactMessageRepository.findByEmailEquals(email, pageable)
                .map(contactMessageMapper::contactMessageToResponse);
    }

    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {
        // Pageable nesnesini oluştur
        Pageable pageable = contactMessageMapper.createPageable(page, size, sort, type);

        // Konuya göre arama yap ve dönüşüm yap
        return contactMessageRepository.findBySubjectEquals(subject, pageable)
                .map(contactMessageMapper::contactMessageToResponse);
    }

    public String deleteById(Long id) {
        // İd ile mesajı al ve sil
        getContactMessageById(id);
        contactMessageRepository.deleteById(id);
        return Messages.CONTACT_MESSAGE_DELETED_SUCCESSFULLY;
    }

    public ContactMessage getContactMessageById(Long id) {
        // İd ile mesajı bul, bulunamazsa hata fırlat
        return contactMessageRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_MESSAGE));
    }

    public List<ContactMessage> searchBetweenDates(String beginDateString, String endDateString) {
        try {
            // Tarihleri LocalDate türüne çevir
            LocalDate beginDate = LocalDate.parse(beginDateString);
            LocalDate endDate = LocalDate.parse(endDateString);

            // Tarihler arasında mesajları ara
            return contactMessageRepository.findMessagesBetweenDates(beginDate, endDate);
        } catch (DateTimeParseException e) {
            throw new ConflictException(Messages.WRONG_DATE_MESSAGE);
        }
    }

    public List<ContactMessage> searchBetweenTimes(String startHourString, String startMinuteString, String endHourString, String endMinuteString) {
        try {
            // Saat ve dakikaları Integer türüne çevir
            int startHour = Integer.parseInt(startHourString);
            int startMinute = Integer.parseInt(startMinuteString);
            int endHour = Integer.parseInt(endHourString);
            int endMinute = Integer.parseInt(endMinuteString);

            // Saatler arasında mesajları ara
            return contactMessageRepository.findMessagesBetweenTimes(startHour, startMinute, endHour, endMinute);
        } catch (NumberFormatException e) {
            throw new ConflictException(Messages.WRONG_TIME_MESSAGE);
        }
    }
}
