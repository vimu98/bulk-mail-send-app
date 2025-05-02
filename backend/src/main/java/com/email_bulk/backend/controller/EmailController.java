package com.email_bulk.backend.controller;

import com.email_bulk.backend.dto.EmailRequest;
import com.email_bulk.backend.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkEmails(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendBulkEmails(emailRequest);
            return ResponseEntity.ok("Emails sent successfully");
        } catch (MessagingException | IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Failed to send emails: " + e.getMessage());
        }
    }
}