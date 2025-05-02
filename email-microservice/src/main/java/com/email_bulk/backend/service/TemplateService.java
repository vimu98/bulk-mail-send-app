package com.email_bulk.backend.service;

import com.email_bulk.backend.entity.Template;
import org.springframework.stereotype.Component;

import java.util.List;

public interface TemplateService {
    Template saveTemplate(String name, String htmlContent);

    Template getTemplate(Long id);

    Template getTemplateByName(String name);

    List<Template> getAllTemplates();
}
