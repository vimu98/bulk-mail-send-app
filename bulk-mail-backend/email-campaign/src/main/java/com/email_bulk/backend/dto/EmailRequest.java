package com.email_bulk.backend.dto;

public class EmailRequest {
    private Long templateId;
    private Long emailListId;
    private String subject;
    private String htmlContent;

    // Getters and Setters
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