package com.email_bulk.backend.controller;

import com.email_bulk.backend.dto.EmailSendDTO;
import com.email_bulk.backend.entity.EmailCampaign;
import com.email_bulk.backend.service.EmailCampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final TemplateService templateService;
    private final EmailListService emailListService;
    private final EmailCampaignService campaignService;

    public EmailController(TemplateService templateService, EmailListService emailListService,
                           EmailCampaignService campaignService) {
        this.templateService = templateService;
        this.emailListService = emailListService;
        this.campaignService = campaignService;
    }

    @PostMapping(value = "/send-bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailSendDTO.SendBulkResponseDTO> sendBulkEmails(
            @Valid @RequestBody EmailSendDTO.SendBulkRequestDTO request) {
        try {

            EmailList emailList = emailListService.getEmailList(request.getEmailListId());

            EmailCampaign campaign = campaignService.recordCampaign(
                    request.getTemplateId(), request.getEmailListId(), request.getSubject(), request.getHtmlContent(), emailList);

            EmailSendDTO.SendBulkResponseDTO response = new EmailSendDTO.SendBulkResponseDTO(
                    "Emails sent and campaign recorded with ID: " + campaign.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EmailSendDTO.SendBulkResponseDTO("Failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailSendDTO.SendBulkResponseDTO("Failed: " + e.getMessage()));
        }
    }
}