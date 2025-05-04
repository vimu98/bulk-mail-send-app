package com.email_bulk.backend.controller;

import com.email_bulk.backend.dto.TemplateDTO;
import com.email_bulk.backend.entity.Template;
import com.email_bulk.backend.service.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
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
    public ResponseEntity<TemplateDTO.TemplateResponseDTO> saveTemplate(
            @Valid @RequestBody TemplateDTO.TemplateRequestDTO request) {
        try {
            if (request.getDesign() == null || !request.getDesign().has("body") ||
                    !request.getDesign().get("body").has("rows")) {
                return ResponseEntity.badRequest()
                        .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Invalid design JSON"));
            }
            String designString = objectMapper.writeValueAsString(request.getDesign());
            Template savedTemplate = templateService.saveTemplate(request.getName(), designString);
            TemplateDTO.TemplateResponseDTO response = new TemplateDTO.TemplateResponseDTO(
                    savedTemplate.getId(), savedTemplate.getName(), objectMapper.readTree(savedTemplate.getDesign()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Failed to save template"));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemplateDTO.TemplateResponseDTO> updateTemplate(
            @PathVariable Long id, @Valid @RequestBody TemplateDTO.TemplateRequestDTO request) {
        try {
            if (request.getDesign() == null || !request.getDesign().has("body") ||
                    !request.getDesign().get("body").has("rows")) {
                return ResponseEntity.badRequest()
                        .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Invalid design JSON"));
            }
            String designString = objectMapper.writeValueAsString(request.getDesign());
            Template updatedTemplate = templateService.updateTemplate(id, request.getName(), designString);
            TemplateDTO.TemplateResponseDTO response = new TemplateDTO.TemplateResponseDTO(
                    updatedTemplate.getId(), updatedTemplate.getName(), objectMapper.readTree(updatedTemplate.getDesign()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Failed to update template"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDTO.TemplateResponseDTO> getTemplate(@PathVariable Long id) {
        try {
            Template template = templateService.getTemplate(id);
            TemplateDTO.TemplateResponseDTO response = new TemplateDTO.TemplateResponseDTO(
                    template.getId(), template.getName(), objectMapper.readTree(template.getDesign()));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Failed to fetch template"));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TemplateDTO.TemplateResponseDTO> getTemplateByName(@PathVariable String name) {
        try {
            Template template = templateService.getTemplateByName(name);
            TemplateDTO.TemplateResponseDTO response = new TemplateDTO.TemplateResponseDTO(
                    template.getId(), template.getName(), objectMapper.readTree(template.getDesign()));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TemplateDTO.TemplateResponseDTO(null, null, null, "Failed to fetch template"));
        }
    }

    @GetMapping
    public ResponseEntity<List<TemplateDTO.TemplateResponseDTO>> getAllTemplates() {
        try {
            List<Template> templates = templateService.getAllTemplates();
            List<TemplateDTO.TemplateResponseDTO> responses = templates.stream().map(template -> {
                try {
                    return new TemplateDTO.TemplateResponseDTO(
                            template.getId(), template.getName(), objectMapper.readTree(template.getDesign()));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse design JSON: " + e.getMessage());
                }
            }).collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}