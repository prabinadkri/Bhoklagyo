package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.DocumentRequest;
import com.example.Bhoklagyo.dto.DocumentResponse;
import com.example.Bhoklagyo.entity.Document;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.repository.DocumentRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public DocumentResponse createDocument(DocumentRequest documentRequest) {
        Document document = new Document();
        document.setDocType(documentRequest.getDocType());
        document.setFileLocationUrl(documentRequest.getFileLocationUrl());
        document.setExpiryDate(documentRequest.getExpiryDate());
        document.setDocumentNumber(documentRequest.getDocumentNumber());
        document.setIssuedDate(documentRequest.getIssuedDate());

        Document savedDocument = documentRepository.save(document);
        return mapToResponse(savedDocument);
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return mapToResponse(document);
    }

    @Override
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponse> getDocumentsByRestaurantId(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return documentRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found with id: " + id);
        }
        documentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DocumentResponse linkDocumentToRestaurant(Long documentId, Long restaurantId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        document.getRestaurants().add(restaurant);
        restaurant.getDocuments().add(document);
        
        documentRepository.save(document);
        return mapToResponse(document);
    }

    @Override
    @Transactional
    public DocumentResponse unlinkDocumentFromRestaurant(Long documentId, Long restaurantId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        document.getRestaurants().remove(restaurant);
        restaurant.getDocuments().remove(document);
        
        documentRepository.save(document);
        return mapToResponse(document);
    }

    private DocumentResponse mapToResponse(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setDocId(document.getDocId());
        response.setDocType(document.getDocType());
        response.setFileLocationUrl(document.getFileLocationUrl());
        response.setExpiryDate(document.getExpiryDate());
        response.setDocumentNumber(document.getDocumentNumber());
        response.setIssuedDate(document.getIssuedDate());
        response.setRestaurantIds(
            document.getRestaurants().stream()
                .map(Restaurant::getId)
                .collect(Collectors.toSet())
        );
        return response;
    }
}
