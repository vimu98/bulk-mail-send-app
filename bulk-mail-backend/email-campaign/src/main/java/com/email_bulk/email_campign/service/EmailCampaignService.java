package com.email_bulk.email_campign.service;

import com.email_bulk.email_campign.entity.EmailCampaign;
import jakarta.mail.MessagingException;

public interface EmailCampaignService {
    EmailCampaign recordCampaign(Long templateId, Long emailListId, String subject, String htmlContent) throws MessagingException;
}