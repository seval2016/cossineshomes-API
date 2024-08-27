package com.project.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetPasswordCode) {

        // Create an email message object
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient email address
        message.setTo(toEmail);

        // Set the subject of the email
        message.setSubject("Password Reset Request");

        // Set the body of the email
        String body = "To reset your password, use the following code: " + resetPasswordCode +
                "\n\nIf you did not request this, please ignore this email.";
        message.setText(body);

        // Send the email
        mailSender.send(message);


    }
}
