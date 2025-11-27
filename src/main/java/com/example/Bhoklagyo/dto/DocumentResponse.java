package com.example.Bhoklagyo.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DocumentResponse {
    
    private Long docId;
    private String docType;
    private String fileLocationUrl;
    private LocalDate expiryDate;
    private String documentNumber;
    private LocalDate issuedDate;
    private Set<Long> restaurantIds = new HashSet<>();

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

    public Set<Long> getRestaurantIds() {
        return restaurantIds;
    }

    public void setRestaurantIds(Set<Long> restaurantIds) {
        this.restaurantIds = restaurantIds;
    }
}
