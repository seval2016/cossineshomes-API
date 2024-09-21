package com.project.contactmessage.repository;

import com.project.contactmessage.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {

    // Status ile tüm mesajları getir
    Page<ContactMessage> findByStatus(int status, Pageable pageable);

    // Status ve query ile tüm mesajları getir
    Page<ContactMessage> findByStatusAndMessageContaining(int status, String query, Pageable pageable);

}