package com.project.utils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
public class MailUtil {
    private static final String LOGO_PATH = "/static/logo.png";

    public static MimeMessagePreparator buildRegistrationEmail(String recipientEmail, String recipientName) {
        return mimeMessage -> {
            // Bir MimeMessageHelper nesnesi oluşturur. Bu yardımcı sınıf, MIME mesajını daha kolay hazırlamaya yarar.
            // İkinci parametre true olarak ayarlanmış, bu da mesajın HTML içeriği olabileceğini belirtir.
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Cossinest Homes - Confirm your email address");

            // cid:logo: e-postaya gömülü resmin referansıdır.
            String message = String.format("<html><body style='background-color: lightgray; padding: 5px; border: 2px solid darkgray; border-radius: 5px;'>" +
                            "<img src='cid:logo' alt='Cossinest Homes Logo' style='width: 135px; height: auto;'><br>" +
                            "<p>Dear "+ recipientName +", </p>" +
                            "<p>Thank you for registering with Cossinest Homes.</p>" +
                            "<p><small>If you did not register with Cossinest Homes, please ignore this email.</small></p>" +
                            "<p style='text-align: center; font-size: 10px'>Copyright &copy; Cossinest Homes 2024. Tüm hakları saklıdır.</p>" +
                            "</body></html>",
                    recipientName
            );


            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildResetPasswordEmail(String recipientEmail, String resetToken , String recipientName) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Cossinest Homes - Reset your password");

            String message = "<html><body style='background-color: lightgray; padding: 5px; border: 2px solid darkgray; border-radius: 5px;'>" +
                    "<img src='cid:logo' alt='Cossinest Homes Logo' style='width: 135px; height: auto;'><br>" +
                    "<p>Dear "+ recipientName +", </p>" +
                    "<p>You have requested to reset your password. You can use below code to reset your password:</p>" +
                    "<p><strong>Your code: " + resetToken + "</strong></p>" +
                    "<p><small>If you did not request a password reset, please ignore this email.</small></p>" +
                    "<p style='text-align: center; font-size: 10px'>Copyright &copy; Cossinest Homes 2024. Tüm hakları saklıdır.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }
}
