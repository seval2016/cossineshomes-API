package com.project.repository.business;

import com.project.entity.concretes.business.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.DoubleStream;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Long> {
    // Status ile tüm mesajları getir
    Page<Contact> findByStatus(int status, Pageable pageable);

    // Status ve query ile tüm mesajları getir
    Page<Contact> findByStatusAndMessageContaining(int status, String query, Pageable pageable);
}