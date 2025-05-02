package com.email_bulk.backend.service;

import com.email_bulk.backend.dto.EmailRequest;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendBulkEmails(EmailRequest emailRequest) throws MessagingException;
}
