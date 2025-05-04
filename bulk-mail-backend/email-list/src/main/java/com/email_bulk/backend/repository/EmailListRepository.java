package com.email_bulk.backend.repository;

import com.email_bulk.backend.entity.EmailList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailListRepository extends JpaRepository<EmailList, Long> {
    Optional<EmailList> findByName(String name);
}