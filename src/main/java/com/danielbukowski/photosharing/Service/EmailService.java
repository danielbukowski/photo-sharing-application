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

    private final JavaMailSender emailSender;

    @Async
    public void sendEmailVerificationMessage(String emailTo,  String nickname, UUID verificationToken) {
        String text = """
                Hi %s!<br><br>
                                
                This is your verification token: "%s" to complete your registration<br><br>
                                
                Best regards,
                XYZ
                """.formatted(nickname, verificationToken);
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
    public void sendEmailForCompletedRegistration(String emailTo, String nickname) {
        String text = """
                Hi %s!<br><br>
                                
                Your registration has been successful<br><br>
                                
                Best regards,
                XYZ
                """.formatted(nickname);
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

    public void sendEmailWithResetPasswordToken(String emailTo, String nickname, UUID resetPasswordToken) {
        String text = """
                Hi %s!<br><br>
                                
                You have request a password reset.<br>
                This is your password reset token: "%s"<br><br>
                                
                Best regards,
                XYZ
                """.formatted(nickname, resetPasswordToken);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(emailTo);
            helper.setSubject("Your password reset token is here");
            helper.setText(text, true);

            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send an email for completed registration", e);
            throw new RuntimeException("failed to send an email");
        }
    }

    public void sendPasswordResetNotification(String emailTo, String nickname) {
        String text = """
                Hi %s!<br><br>
                                
                Your password has been changed by the password reset token<br><br>
                                
                Best regards,
                XYZ
                """.formatted(nickname);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(emailTo);
            helper.setSubject("Your password has changed");
            helper.setText(text, true);

            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send an email for completed registration", e);
            throw new RuntimeException("failed to send an email");
        }
    }

}
