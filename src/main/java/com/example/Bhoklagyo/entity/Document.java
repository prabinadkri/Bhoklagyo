package com.example.Bhoklagyo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long docId;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(name = "file_location_url", nullable = false)
    private String fileLocationUrl;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @ManyToMany
    @JoinTable(
        name = "restaurant_documents",
        joinColumns = @JoinColumn(name = "doc_id"),
        inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    @JsonIgnore
    private Set<Restaurant> restaurants = new HashSet<>();

    public Document() {}

    public Document(String docType, String fileLocationUrl, LocalDate expiryDate) {
        this.docType = docType;
        this.fileLocationUrl = fileLocationUrl;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFileLocationUrl() {
        return fileLocationUrl;
    }

    public void setFileLocationUrl(String fileLocationUrl) {
        this.fileLocationUrl = fileLocationUrl;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
