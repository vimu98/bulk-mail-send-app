package com.email_bulk.backend.repository;

import com.email_bulk.backend.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByName(String name);
}