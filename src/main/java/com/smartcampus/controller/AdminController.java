package com.smartcampus.controller;

import com.smartcampus.model.User;
import com.smartcampus.repository.AcademicRecordRepository;
import com.smartcampus.repository.DocumentRepository;
import com.smartcampus.repository.PaymentRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final StudentService studentService;
    private final AcademicRecordRepository academicRecordRepository;
    private final PaymentRepository paymentRepository;
    private final DocumentRepository documentRepository;

    public AdminController(UserRepository userRepository, StudentService studentService,
                            AcademicRecordRepository academicRecordRepository,
                            PaymentRepository paymentRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.academicRecordRepository = academicRecordRepository;
        this.paymentRepository = paymentRepository;
        this.documentRepository = documentRepository;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        model.addAttribute("recordCount", academicRecordRepository.count());
        model.addAttribute("totalCollected", paymentRepository.findAll().stream()
                .mapToDouble(p -> p.getAmount()).sum());
        model.addAttribute("documentCount", documentRepository.count());
        model.addAttribute("recentStudents", studentService.getAllStudents().stream().limit(5).toList());
        return "admin";
    }

    // Link a login account to a student record, so a STUDENT user can reach
    // /students/me and /academic/me. Previously this required a manual SQL
    // UPDATE — this page replaces that.
    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin-users";
    }

    @PostMapping("/admin/users/{id}/link")
    public String link(@PathVariable Long id, @RequestParam(required = false) Long studentId,
                        RedirectAttributes redirect) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("User " + id + " not found"));
        user.setLinkedStudentId(studentId); // null clears the link
        userRepository.save(user);
        redirect.addFlashAttribute("message",
                studentId == null ? "Link cleared." : "Account linked to student " + studentId + ".");
        return "redirect:/admin/users";
    }
}
