package com.email_bulk.email_template.service;

import com.email_bulk.email_template.entity.Template;

import java.util.List;

public interface TemplateService {
    Template saveTemplate(String name, String design);
    Template updateTemplate(Long id, String name, String design);
    Template getTemplate(Long id);
    Template getTemplateByName(String name);
    List<Template> getAllTemplates();
    void deleteTemplate(Long id);
}