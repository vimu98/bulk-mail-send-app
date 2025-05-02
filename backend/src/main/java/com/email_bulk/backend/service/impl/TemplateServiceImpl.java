package com.email_bulk.backend.service.impl;

import com.email_bulk.backend.entity.Template;
import com.email_bulk.backend.repository.TemplateRepository;
import com.email_bulk.backend.service.TemplateService;
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
        if (templateRepository.findByName(name) != null) {
            throw new IllegalArgumentException("Template with name '" + name + "' already exists");
        }
        Template template = new Template();
        template.setName(name);
        template.setDesign(design);
        return templateRepository.save(template);
    }

    @Override
    public Template getTemplate(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    @Override
    public Template getTemplateByName(String name) {
        Template template = templateRepository.findByName(name);
        if (template == null) {
            throw new RuntimeException("Template not found with name: " + name);
        }
        return template;
    }

    @Override
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }
}