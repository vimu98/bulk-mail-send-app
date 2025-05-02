package com.email_bulk.backend.controller;

import com.email_bulk.backend.entity.Template;
import com.email_bulk.backend.service.TemplateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final ObjectMapper objectMapper;

    public TemplateController(TemplateService templateService, ObjectMapper objectMapper) {
        this.templateService = templateService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemplateResponse> saveTemplate(@Valid @RequestBody TemplateRequest request) {
        try {
            JsonNode design = request.getDesign();
            if (design == null || !design.has("body") || !design.get("body").has("rows")) {
                return ResponseEntity.badRequest().body(null);
            }
            String designString = objectMapper.writeValueAsString(design);
            Template savedTemplate = templateService.saveTemplate(request.getName(), designString);
            JsonNode designNode = objectMapper.readTree(savedTemplate.getDesign());
            TemplateResponse response = new TemplateResponse(savedTemplate.getId(), savedTemplate.getName(), designNode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplate(@PathVariable Long id) {
        try {
            Template template = templateService.getTemplate(id);
            JsonNode designNode = objectMapper.readTree(template.getDesign());
            TemplateResponse response = new TemplateResponse(template.getId(), template.getName(), designNode);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TemplateResponse> getTemplateByName(@PathVariable String name) {
        try {
            Template template = templateService.getTemplateByName(name);
            JsonNode designNode = objectMapper.readTree(template.getDesign());
            TemplateResponse response = new TemplateResponse(template.getId(), template.getName(), designNode);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        try {
            List<Template> templates = templateService.getAllTemplates();
            List<TemplateResponse> responses = templates.stream().map(template -> {
                try {
                    JsonNode designNode = objectMapper.readTree(template.getDesign());
                    return new TemplateResponse(template.getId(), template.getName(), designNode);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse design JSON: " + e.getMessage());
                }
            }).collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // DTO for POST request
    public static class TemplateRequest {
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

    // DTO for response
    public static class TemplateResponse {
        private Long id;
        private String name;
        private JsonNode design;

        public TemplateResponse(Long id, String name, JsonNode design) {
            this.id = id;
            this.name = name;
            this.design = design;
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
    }
}