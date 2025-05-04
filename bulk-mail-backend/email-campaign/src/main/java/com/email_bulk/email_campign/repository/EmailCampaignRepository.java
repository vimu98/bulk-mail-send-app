package com.email_bulk.email_campign.repository;

import com.email_bulk.email_campign.entity.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailCampaignRepository extends JpaRepository<EmailCampaign, Long> {
}