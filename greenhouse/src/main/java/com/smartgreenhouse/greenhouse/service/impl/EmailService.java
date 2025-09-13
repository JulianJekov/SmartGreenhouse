package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.exceptions.EmailException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.verification.subject}")
    private String verificationSubject;

    @Value("${app.email.password-reset.subject}")
    private String passwordResetSubject;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(User user, String verificationToken) {
        String verificationUrl = "http://localhost:8080/api/user/verify-email?token=" + verificationToken;

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("verificationUrl", verificationUrl);

        String htmlContent = templateEngine.process("email-verification", context);

        sendEmail(user.getEmail(), verificationSubject, htmlContent);
    }

    public void sendPasswordResetEmail(User user, String passwordResetToken) {
        String resetUrl = "http://localhost:8080/api/user/reset-password?token=" + passwordResetToken;

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("resetUrl", resetUrl);

        String htmlContent = templateEngine.process("password-reset", context);

        sendEmail(user.getEmail(), passwordResetSubject, htmlContent);

    }

    public void sendWateringNotification(String toEmail, String name, Long greenhouseId, String greenhouseName,
                                         WateringSource source, boolean success, Double amount, int attempts,
                                         String errorDetails) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("success", success);
        context.setVariable("source", source);
        context.setVariable("greenhouseId", greenhouseId);
        context.setVariable("greenhouseName", greenhouseName);
        context.setVariable("amount", amount);
        context.setVariable("attempts", attempts);
        context.setVariable("time", Instant.now().toString());
        context.setVariable("errorDetails", errorDetails);

        String htmlContent = templateEngine.process("watering-notification", context);
        String subject = String.format("Watering %s - %s",
                success ? "Successful" : "Failed",
                source.equals(WateringSource.MANUAL) ? "Manual Watering" : "Auto Watering");

        sendEmail(toEmail, subject, htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        }catch(Exception e){
            throw new EmailException("Failed to send Email to: " + to, e);
        }
    }


}
