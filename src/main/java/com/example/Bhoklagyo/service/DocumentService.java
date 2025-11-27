package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.DocumentRequest;
import com.example.Bhoklagyo.dto.DocumentResponse;
import java.util.List;

public interface DocumentService {
    DocumentResponse createDocument(DocumentRequest documentRequest);
    DocumentResponse getDocumentById(Long id);
    List<DocumentResponse> getAllDocuments();
    List<DocumentResponse> getDocumentsByRestaurantId(Long restaurantId);
    void deleteDocument(Long id);
    DocumentResponse linkDocumentToRestaurant(Long documentId, Long restaurantId);
    DocumentResponse unlinkDocumentFromRestaurant(Long documentId, Long restaurantId);
}
