package com.project.contactmessage.repository;

import com.project.contactmessage.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // Status ile tüm mesajları getir
    Page<ContactMessage> findByStatus(int status, Pageable pageable);

    // Status ve query ile tüm mesajları getir
    Page<ContactMessage> findByStatusAndMessageContaining(int status, String query, Pageable pageable);

}