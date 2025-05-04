package com.email_bulk.email_template.service.impl;

import com.email_bulk.email_template.entity.Template;
import com.email_bulk.email_template.repository.TemplateRepository;
import com.email_bulk.email_template.service.TemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Template saveTemplate(String name, String design) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        if (design == null) {
            throw new IllegalArgumentException("Template design cannot be null");
        }
        if (templateRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Template with name '" + name + "' already exists");
        }
        Template template = new Template();
        template.setName(name);
        template.setDesign(design);
        return templateRepository.save(template);
    }

    @Override
    public Template updateTemplate(Long id, String name, String design) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        if (design == null) {
            throw new IllegalArgumentException("Template design cannot be null");
        }
        Template existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        templateRepository.findByName(name)
                .ifPresent(template -> {
                    if (!template.getId().equals(id)) {
                        throw new IllegalArgumentException("Template with name '" + name + "' already exists");
                    }
                });
        existingTemplate.setName(name);
        existingTemplate.setDesign(design);
        return templateRepository.save(existingTemplate);
    }

    @Override
    public Template getTemplate(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    @Override
    public Template getTemplateByName(String name) {
        return templateRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Template not found with name: " + name));
    }

    @Override
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }
}