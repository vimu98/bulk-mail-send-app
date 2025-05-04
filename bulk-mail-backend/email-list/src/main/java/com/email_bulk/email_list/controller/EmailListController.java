package com.email_bulk.email_list.controller;

import com.email_bulk.email_list.dto.EmailListDTO;
import com.email_bulk.email_list.entity.EmailList;
import com.email_bulk.email_list.service.EmailListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email-lists")
public class EmailListController {

    private final EmailListService emailListService;

    public EmailListController(EmailListService emailListService) {
        this.emailListService = emailListService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailListDTO.EmailListResponseDTO> createEmailList(
            @Valid @RequestBody EmailListDTO.EmailListRequestDTO request) {
        try {
            EmailList emailList = emailListService.createEmailList(request.getName(), request.getRecipients());
            EmailListDTO.EmailListResponseDTO response = new EmailListDTO.EmailListResponseDTO(
                    emailList.getId(), emailList.getName(), emailList.getRecipients());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, "Failed to create email list"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailListDTO.EmailListResponseDTO> getEmailList(@PathVariable Long id) {
        try {
            System.out.println(id);
            EmailList emailList = emailListService.getEmailList(id);
            EmailListDTO.EmailListResponseDTO response = new EmailListDTO.EmailListResponseDTO(
                    emailList.getId(), emailList.getName(), emailList.getRecipients());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<EmailListDTO.EmailListResponseDTO>> getAllEmailLists() {
        try {
            List<EmailList> emailLists = emailListService.getAllEmailLists();
            List<EmailListDTO.EmailListResponseDTO> responses = emailLists.stream()
                    .map(list -> new EmailListDTO.EmailListResponseDTO(
                            list.getId(), list.getName(), list.getRecipients()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailListDTO.EmailListResponseDTO> updateEmailList(
            @PathVariable Long id, @Valid @RequestBody EmailListDTO.EmailListRequestDTO request) {
        try {
            EmailList updatedList = emailListService.updateEmailList(id, request.getName(), request.getRecipients());
            EmailListDTO.EmailListResponseDTO response = new EmailListDTO.EmailListResponseDTO(
                    updatedList.getId(), updatedList.getName(), updatedList.getRecipients());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailListDTO.EmailListResponseDTO(null, null, null, "Failed to update email list"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmailList(@PathVariable Long id) {
        try {
            emailListService.deleteEmailList(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}