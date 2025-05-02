package com.email_bulk.backend.service.impl;

import com.email_bulk.backend.dto.EmailRequest;
import com.email_bulk.backend.entity.Template;
import com.email_bulk.backend.repository.TemplateRepository;
import com.email_bulk.backend.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateRepository templateRepository;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateRepository templateRepository) {
        this.mailSender = mailSender;
        this.templateRepository = templateRepository;
    }

    public void sendBulkEmails(EmailRequest emailRequest) throws MessagingException {
        // Use HTML content from request
        String htmlContent = emailRequest.getHtmlContent();
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content is required");
        }

        List<String> recipients = emailRequest.getRecipients();
        String subject = emailRequest.getSubject();

        for (String recipient : recipients) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            System.out.println(recipient);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content// Replace with your sender email

            mailSender.send(message);
        }
    }
}