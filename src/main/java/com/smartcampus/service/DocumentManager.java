package com.smartcampus.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * SDD: DocumentManager — handles the actual file I/O (uploadDocument,
 * retrieveDocument), kept separate from DocumentService which owns the
 * database metadata.
 *
 * Files are stored under ./uploads/{studentId}/ relative to wherever the
 * app runs from. ASSUMPTION: local disk storage — fine for a student
 * project; a production system would use cloud storage instead.
 */
@Component
public class DocumentManager {

    private static final String UPLOAD_ROOT = "uploads";
    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    public String uploadDocument(Long studentId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("No file was selected");
        if (file.getSize() > MAX_SIZE_BYTES) throw new IllegalArgumentException("File exceeds the 10MB limit");

        Path dir = Paths.get(UPLOAD_ROOT, String.valueOf(studentId));
        Files.createDirectories(dir);

        String safeName = UUID.randomUUID() + "_" + Paths.get(file.getOriginalFilename()).getFileName();
        Path target = dir.resolve(safeName);
        file.transferTo(target);
        return target.toString();
    }

    public InputStream retrieveDocument(String storedPath) throws IOException {
        return Files.newInputStream(Paths.get(storedPath));
    }

    public void deleteDocument(String storedPath) throws IOException {
        Files.deleteIfExists(Paths.get(storedPath));
    }
}
