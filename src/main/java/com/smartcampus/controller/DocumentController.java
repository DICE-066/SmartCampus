package com.smartcampus.controller;

import com.smartcampus.model.Document;
import com.smartcampus.model.User;
import com.smartcampus.service.AuthService;
import com.smartcampus.service.DocumentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final AuthService authService;

    public DocumentController(DocumentService documentService, AuthService authService) {
        this.documentService = documentService;
        this.authService = authService;
    }

    // Reachable by ADMIN or STUDENT — ownership enforced here.
    @GetMapping
    public String list(@RequestParam(required = false) Long studentId, Model model, Authentication authentication) {
        if (studentId != null) {
            if (!authService.canAccessStudent(authentication, studentId)) {
                throw new AccessDeniedException("Not permitted to view this student's documents.");
            }
            model.addAttribute("studentId", studentId);
            model.addAttribute("documents", documentService.getStudentDocuments(studentId));
        }
        return "documents";
    }

    @GetMapping("/upload")
    public String uploadForm(@RequestParam(required = false) Long studentId, Model model, Authentication authentication) {
        if (studentId != null && !authService.canAccessStudent(authentication, studentId)) {
            throw new AccessDeniedException("Not permitted to upload for this student.");
        }
        model.addAttribute("studentId", studentId);
        return "upload-document";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam Long studentId, @RequestParam("file") MultipartFile file,
                          Authentication authentication, RedirectAttributes redirect) {
        if (!authService.canAccessStudent(authentication, studentId)) {
            throw new AccessDeniedException("Not permitted to upload for this student.");
        }
        try {
            documentService.uploadDocument(studentId, file);
            redirect.addFlashAttribute("message", "Document uploaded.");
        } catch (IllegalArgumentException | IOException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/documents/upload?studentId=" + studentId;
        }
        return "redirect:/documents?studentId=" + studentId;
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id, Authentication authentication) throws IOException {
        Document doc = documentService.getDocument(id);
        if (!authService.canAccessStudent(authentication, doc.getStudentId())) {
            throw new AccessDeniedException("Not permitted to download this document.");
        }
        InputStreamResource resource = new InputStreamResource(documentService.retrieveDocument(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    // STUDENT landing page.
    @GetMapping("/me")
    public String me(Authentication authentication, RedirectAttributes redirect) {
        User user = authService.currentUser(authentication);
        if (user == null || user.getLinkedStudentId() == null) {
            redirect.addFlashAttribute("error",
                    "No student profile is linked to your account yet. Ask an administrator to link it.");
            return "redirect:/";
        }
        return "redirect:/documents?studentId=" + user.getLinkedStudentId();
    }
}
