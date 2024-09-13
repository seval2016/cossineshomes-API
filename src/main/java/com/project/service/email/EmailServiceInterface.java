package com.project.service.email;

import org.springframework.mail.javamail.MimeMessagePreparator;

public interface EmailServiceInterface {

    void sendEmail(MimeMessagePreparator mimeMessagePreparator);
}