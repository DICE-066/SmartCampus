package com.smartcampus.service;

import com.smartcampus.model.Document;
import com.smartcampus.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final DocumentManager documentManager;

    public DocumentService(DocumentRepository repository, DocumentManager documentManager) {
        this.repository = repository;
        this.documentManager = documentManager;
    }

    public Document uploadDocument(Long studentId, MultipartFile file) throws IOException {
        String storedPath = documentManager.uploadDocument(studentId, file);
        Document doc = new Document(studentId, file.getOriginalFilename(), storedPath, LocalDateTime.now());
        return repository.save(doc);
    }

    public InputStream retrieveDocument(Long documentId) throws IOException {
        Document doc = repository.findById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Document " + documentId + " not found"));
        return documentManager.retrieveDocument(doc.getStoredPath());
    }

    public Document getDocument(Long documentId) {
        return repository.findById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Document " + documentId + " not found"));
    }

    public List<Document> getStudentDocuments(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    public void deleteDocument(Long documentId) throws IOException {
        Document doc = getDocument(documentId);
        documentManager.deleteDocument(doc.getStoredPath());
        repository.deleteById(documentId);
    }
}
