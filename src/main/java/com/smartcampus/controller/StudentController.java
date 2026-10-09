package com.smartcampus.controller;

import com.smartcampus.model.Student;
import com.smartcampus.model.User;
import com.smartcampus.service.AuthService;
import com.smartcampus.service.StudentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;
    private final AuthService authService;

    public StudentController(StudentService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    // ADMIN only (enforced in SecurityConfig): full list.
    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", service.getAllStudents());
        return "students";
    }

    // ADMIN only.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    // ADMIN only.
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", service.getStudent(id));
        return "student-form";
    }

    // ADMIN only.
    @PostMapping("/save")
    public String save(@ModelAttribute Student student, RedirectAttributes redirect) {
        try {
            if (student.getStudentId() == null) {
                service.registerStudent(student);
                redirect.addFlashAttribute("message", "Student registered.");
            } else {
                service.updateStudent(student.getStudentId(), student);
                redirect.addFlashAttribute("message", "Student updated.");
            }
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/students/" + (student.getStudentId() == null ? "new" : "edit/" + student.getStudentId());
        }
        return "redirect:/students";
    }

    // ADMIN only.
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.deleteStudent(id);
        redirect.addFlashAttribute("message", "Student deleted.");
        return "redirect:/students";
    }

    // Reachable by ADMIN or STUDENT (SecurityConfig), but a STUDENT may only
    // view the record linked to their own account — enforced here.
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, Authentication authentication) {
        if (!authService.canAccessStudent(authentication, id)) {
            throw new AccessDeniedException("Not permitted to view this student record.");
        }
        model.addAttribute("student", service.getStudent(id));
        return "student-details";
    }

    // ADMIN only — a STUDENT searching the whole student body isn't in the
    // permission matrix (students only get "My Information").
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String studentNumber, Model model) {
        if (studentNumber != null && !studentNumber.isBlank()) {
            model.addAttribute("results", service.searchStudent(studentNumber));
            model.addAttribute("searched", true);
        }
        return "student-search";
    }

    // STUDENT landing page: resolves their own linked record and redirects there.
    @GetMapping("/me")
    public String me(Authentication authentication, RedirectAttributes redirect) {
        User user = authService.currentUser(authentication);
        if (user == null || user.getLinkedStudentId() == null) {
            redirect.addFlashAttribute("error",
                    "No student profile is linked to your account yet. Ask an administrator to link it.");
            return "redirect:/";
        }
        return "redirect:/students/" + user.getLinkedStudentId();
    }
}
