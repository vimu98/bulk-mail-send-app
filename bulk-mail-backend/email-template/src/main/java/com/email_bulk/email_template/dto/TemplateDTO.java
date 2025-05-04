package com.email_bulk.email_template.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TemplateDTO {

    public static class TemplateRequestDTO {
        @NotNull
        @NotEmpty
        private String name;

        @NotNull
        private JsonNode design;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public JsonNode getDesign() {
            return design;
        }

        public void setDesign(JsonNode design) {
            this.design = design;
        }
    }

    public static class TemplateResponseDTO {
        private Long id;
        private String name;
        private JsonNode design;
        private String error;

        public TemplateResponseDTO() {}

        public TemplateResponseDTO(Long id, String name, JsonNode design) {
            this.id = id;
            this.name = name;
            this.design = design;
            this.error = null;
        }

        public TemplateResponseDTO(Long id, String name, JsonNode design, String error) {
            this.id = id;
            this.name = name;
            this.design = design;
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

        public JsonNode getDesign() {
            return design;
        }

        public void setDesign(JsonNode design) {
            this.design = design;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}