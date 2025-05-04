package com.email_bulk.email_campign.service.impl;

import com.email_bulk.email_list.dto.EmailListDTO;
import com.email_bulk.email_campign.entity.EmailCampaign;
import com.email_bulk.email_campign.repository.EmailCampaignRepository;
import com.email_bulk.email_campign.service.EmailCampaignService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailCampaignServiceImpl implements EmailCampaignService {

    private final WebClient emailListClient;

    private final EmailCampaignRepository campaignRepository;

    private final JavaMailSender mailSender;


    public EmailCampaignServiceImpl( WebClient emailListClient,EmailCampaignRepository campaignRepository, JavaMailSender mailSender) {
        this.campaignRepository = campaignRepository;
        this.mailSender = mailSender;
        this.emailListClient = emailListClient;

    }

    @Override
    public EmailCampaign recordCampaign(Long templateId, Long emailListId, String subject, String htmlContent) throws MessagingException {

        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content is required");
        }
        System.out.println(htmlContent);
        try {
            EmailListDTO.EmailListResponseDTO emailList = emailListClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/email-lists/{id}").build(emailListId))
                    .retrieve()
                    .bodyToMono(EmailListDTO.EmailListResponseDTO.class)
                    .block();

            List<String> recipients = emailList.getRecipients();
            System.out.println(recipients);
            for (String recipient : recipients) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                System.out.println(recipient);
                helper.setTo(recipient);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailSender.send(message);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("Email list could not be sent");
        }

        if (templateId == null) {
            throw new IllegalArgumentException("Template ID cannot be null");
        }
        if (emailListId == null) {
            throw new IllegalArgumentException("Email List ID cannot be null");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }

        EmailCampaign campaign = new EmailCampaign();
        campaign.setTemplateId(templateId);
        campaign.setEmailListId(emailListId);
        campaign.setSubject(subject);
        campaign.setSentAt(LocalDateTime.now());
        return campaignRepository.save(campaign);
    }
}