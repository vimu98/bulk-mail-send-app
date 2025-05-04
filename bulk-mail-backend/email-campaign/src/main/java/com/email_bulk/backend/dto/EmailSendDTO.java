package com.email_bulk.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class EmailSendDTO {

    public static class SendBulkRequestDTO {
        @NotNull
        private Long templateId;

        @NotNull
        private Long emailListId;

        @NotEmpty
        private String subject;

        @NotEmpty
        private String htmlContent;

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

        public String getHtmlContent() {
            return htmlContent;
        }

        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }
    }

    public static class SendBulkResponseDTO {
        private String message;

        public SendBulkResponseDTO() {}

        public SendBulkResponseDTO(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}