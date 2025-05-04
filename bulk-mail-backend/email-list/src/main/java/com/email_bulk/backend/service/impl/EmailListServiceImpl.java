package com.email_bulk.backend.service.impl;

import com.email_bulk.backend.entity.EmailList;
import com.email_bulk.backend.repository.EmailListRepository;
import com.email_bulk.backend.service.EmailListService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmailListServiceImpl implements EmailListService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$");

    private final EmailListRepository emailListRepository;

    public EmailListServiceImpl(EmailListRepository emailListRepository) {
        this.emailListRepository = emailListRepository;
    }

    @Override
    public EmailList createEmailList(String name, List<String> recipients) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Email list name cannot be null or empty");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("Recipient list cannot be empty");
        }
        for (String recipient : recipients) {
            if (!EMAIL_PATTERN.matcher(recipient).matches()) {
                throw new IllegalArgumentException("Invalid email address: " + recipient);
            }
        }
        if (emailListRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Email list with name '" + name + "' already exists");
        }

        EmailList emailList = new EmailList();
        emailList.setName(name);
        emailList.setRecipients(recipients);
        return emailListRepository.save(emailList);
    }

    @Override
    public EmailList getEmailList(Long id) {
        return emailListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email list not found with id: " + id));
    }

    @Override
    public List<EmailList> getAllEmailLists() {
        return emailListRepository.findAll();
    }

    @Override
    public EmailList updateEmailList(Long id, String name, List<String> recipients) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Email list name cannot be null or empty");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("Recipient list cannot be empty");
        }
        for (String recipient : recipients) {
            if (!EMAIL_PATTERN.matcher(recipient).matches()) {
                throw new IllegalArgumentException("Invalid email address: " + recipient);
            }
        }
        EmailList existingList = emailListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email list not found with id: " + id));
        emailListRepository.findByName(name)
                .ifPresent(list -> {
                    if (!list.getId().equals(id)) {
                        throw new IllegalArgumentException("Email list with name '" + name + "' already exists");
                    }
                });

        existingList.setName(name);
        existingList.setRecipients(recipients);
        return emailListRepository.save(existingList);
    }

    @Override
    public void deleteEmailList(Long id) {
        if (!emailListRepository.existsById(id)) {
            throw new RuntimeException("Email list not found with id: " + id);
        }
        emailListRepository.deleteById(id);
    }
}