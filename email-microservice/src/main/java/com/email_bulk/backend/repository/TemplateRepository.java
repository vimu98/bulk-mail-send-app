package com.email_bulk.backend.repository;

import com.email_bulk.backend.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Template findByName(String name);
}