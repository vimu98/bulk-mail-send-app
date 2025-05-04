package com.email_bulk.backend.repository;

import com.email_bulk.backend.entity.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailCampaignRepository extends JpaRepository<EmailCampaign, Long> {
}