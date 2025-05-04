package com.email_bulk.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EmailCampaignDTO {

    public static class EmailCampaignResponseDTO {
        private Long id;
        private Long templateId;
        private Long emailListId;
        private String subject;
        private LocalDateTime sentAt;
        private String error;

        public EmailCampaignResponseDTO() {}

        public EmailCampaignResponseDTO(Long id, Long templateId, Long emailListId, String subject, LocalDateTime sentAt) {
            this.id = id;
            this.templateId = templateId;
            this.emailListId = emailListId;
            this.subject = subject;
            this.sentAt = sentAt;
            this.error = null;
        }

        public EmailCampaignResponseDTO(Long id, Long templateId, Long emailListId, String subject, LocalDateTime sentAt, String error) {
            this.id = id;
            this.templateId = templateId;
            this.emailListId = emailListId;
            this.subject = subject;
            this.sentAt = sentAt;
            this.error = error;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Long templateId) {
            this.templateId = templateId;
        }

        public Long getEmailListId() {
            return emailListId;
        }

        public void setEmailListId(Long emailListId) {
            this.emailListId = emailListId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public LocalDateTime getSentAt() {
            return sentAt;
        }

        public void setSentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}