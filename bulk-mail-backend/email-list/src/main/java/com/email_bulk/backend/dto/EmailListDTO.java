package com.email_bulk.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class EmailListDTO {

    public static class EmailListRequestDTO {
        @NotNull
        @NotEmpty
        private String name;

        @NotNull
        @NotEmpty
        private List<String> recipients;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getRecipients() {
            return recipients;
        }

        public void setRecipients(List<String> recipients) {
            this.recipients = recipients;
        }
    }

    public static class EmailListResponseDTO {
        private Long id;
        private String name;
        private List<String> recipients;
        private String error;

        public EmailListResponseDTO() {}

        public EmailListResponseDTO(Long id, String name, List<String> recipients) {
            this.id = id;
            this.name = name;
            this.recipients = recipients;
            this.error = null;
        }

        public EmailListResponseDTO(Long id, String name, List<String> recipients, String error) {
            this.id = id;
            this.name = name;
            this.recipients = recipients;
            this.error = error;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getRecipients() {
            return recipients;
        }

        public void setRecipients(List<String> recipients) {
            this.recipients = recipients;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}