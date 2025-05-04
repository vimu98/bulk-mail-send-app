package com.email_bulk.backend.service;

import com.email_bulk.backend.entity.EmailList;

import java.util.List;

public interface EmailListService {
    EmailList createEmailList(String name, List<String> recipients);
    EmailList getEmailList(Long id);
    List<EmailList> getAllEmailLists();
    EmailList updateEmailList(Long id, String name, List<String> recipients);
    void deleteEmailList(Long id);
}