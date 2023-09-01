package com.danielbukowski.photosharing.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@EnableAsync
@AllArgsConstructor
public class EmailService {

    private static final String HOST = "http://localhost:8080";
    private final JavaMailSender emailSender;

    @Async
    public void sendEmailVerificationMessage(String emailTo, UUID verificationToken) {
        //The link doesn't because the browser sends a GET request
        //Just copy the link and send a POST request via Postman
        String link = HOST + "/api/v2/accounts/email-verification?token=" + verificationToken;
        String text = """
                Hi %s!<br>
                                
                <a href=\"%s\">Click here</a> to complete your registration<br>
                
                Best regards,
                XYZ
                """.formatted(emailTo.split("@")[0], link);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(emailTo);
            helper.setSubject("Please complete your registration");
            helper.setText(text, true);

            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send an email for verification", e);
            throw new RuntimeException("failed to send an email");
        }
    }

    @Async
    public void sendEmailForCompletedRegistration(String emailTo) {
        String text = """
                Hi %s!<br>
                
                Your registration has been successful<br>
                
                Best regards,
                XYZ
                """.formatted(emailTo.split("@")[0]);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(emailTo);
            helper.setSubject("Thank you for your registration");
            helper.setText(text, true);

            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send an email for completed registration", e);
            throw new RuntimeException("failed to send an email");
        }
    }

}
