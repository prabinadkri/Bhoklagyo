package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.DocumentRequest;
import com.example.Bhoklagyo.dto.DocumentResponse;
import com.example.Bhoklagyo.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@PreAuthorize("hasRole('ADMIN')")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(@Valid @RequestBody DocumentRequest documentRequest) {
        DocumentResponse response = documentService.createDocument(documentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        DocumentResponse response = documentService.getDocumentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByRestaurantId(@PathVariable Long restaurantId) {
        List<DocumentResponse> documents = documentService.getDocumentsByRestaurantId(restaurantId);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/{documentId}/link/{restaurantId}")
    public ResponseEntity<DocumentResponse> linkDocumentToRestaurant(@PathVariable Long documentId, 
                                                                     @PathVariable Long restaurantId) {
        DocumentResponse response = documentService.linkDocumentToRestaurant(documentId, restaurantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{documentId}/unlink/{restaurantId}")
    public ResponseEntity<DocumentResponse> unlinkDocumentFromRestaurant(@PathVariable Long documentId, 
                                                                         @PathVariable Long restaurantId) {
        DocumentResponse response = documentService.unlinkDocumentFromRestaurant(documentId, restaurantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
