package com.project.service.email;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService  implements EmailServiceInterface{

    private final JavaMailSender javaMailSender;

    public void sendEmail(MimeMessagePreparator mimeMessagePreparator) {
        javaMailSender.send(mimeMessagePreparator);
    }
}
