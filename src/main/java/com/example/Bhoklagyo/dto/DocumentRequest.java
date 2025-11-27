package com.example.Bhoklagyo.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class DocumentRequest {
    
    @NotBlank(message = "Document type is required")
    private String docType;
    
    @NotBlank(message = "File location URL is required")
    private String fileLocationUrl;
    
    private LocalDate expiryDate;
    
    private String documentNumber;
    
    private LocalDate issuedDate;

    // Getters and Setters
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
}
