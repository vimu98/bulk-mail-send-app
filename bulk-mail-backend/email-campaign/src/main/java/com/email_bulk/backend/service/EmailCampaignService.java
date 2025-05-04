package com.email_bulk.backend.service;

import com.email_bulk.backend.entity.EmailCampaign;
import jakarta.mail.MessagingException;

public interface EmailCampaignService {
    EmailCampaign recordCampaign(Long templateId, Long emailListId, String subject, String htmlContent) throws MessagingException;
}