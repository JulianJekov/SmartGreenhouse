package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.EmailException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
