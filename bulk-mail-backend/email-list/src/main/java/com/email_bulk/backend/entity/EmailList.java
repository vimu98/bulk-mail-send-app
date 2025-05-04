package com.email_bulk.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "email_lists")
public class EmailList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "email_list_recipients", joinColumns = @JoinColumn(name = "email_list_id"))
    @Column(name = "recipient")
    private List<String> recipients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}