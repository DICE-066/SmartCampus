package com.smartcampus.controller;

import com.smartcampus.model.AcademicRecord;
import com.smartcampus.model.User;
import com.smartcampus.service.AcademicService;
import com.smartcampus.service.AuthService;
import com.smartcampus.service.StudentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/academic")
public class AcademicController {

    private final AcademicService academicService;
    private final StudentService studentService;
    private final AuthService authService;

    public AcademicController(AcademicService academicService, StudentService studentService, AuthService authService) {
        this.academicService = academicService;
        this.studentService = studentService;
        this.authService = authService;
    }

    // ADMIN only (SecurityConfig) — generic "look up any student" landing page.
    @GetMapping
    public String landing() {
        return "academic";
    }

    // ADMIN only.
    @GetMapping("/add")
    public String addForm(@RequestParam(required = false) Long studentId, Model model) {
        AcademicRecord record = new AcademicRecord();
        if (studentId != null) record.setStudentId(studentId);
        model.addAttribute("record", record);
        return "add-result";
    }

    // ADMIN only.
    @PostMapping("/save")
    public String save(@ModelAttribute AcademicRecord record, RedirectAttributes redirect) {
        try {
            academicService.addAcademicRecord(record);
            redirect.addFlashAttribute("message", "Result saved.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/academic/add?studentId=" + record.getStudentId();
        }
        return "redirect:/academic/results?studentId=" + record.getStudentId();
    }

    // ADMIN only.
    @PostMapping("/records/{id}/edit")
    public String editRecord(@PathVariable Long id, @RequestParam int credits,
                              @RequestParam double grade, @RequestParam Long studentId,
                              @RequestParam String courseName, RedirectAttributes redirect) {
        AcademicRecord updated = new AcademicRecord(studentId, courseName, credits, grade);
        academicService.updateAcademicRecord(id, updated);
        redirect.addFlashAttribute("message", "Result updated.");
        return "redirect:/academic/results?studentId=" + studentId;
    }

    // Reachable by ADMIN or STUDENT (SecurityConfig), but a STUDENT may only
    // view results for the studentId linked to their own account — enforced here.
    @GetMapping("/results")
    public String results(@RequestParam(required = false) Long studentId, Model model, Authentication authentication) {
        if (studentId != null) {
            if (!authService.canAccessStudent(authentication, studentId)) {
                throw new AccessDeniedException("Not permitted to view this student's academic records.");
            }
            model.addAttribute("student", studentService.getStudent(studentId));
            model.addAttribute("records", academicService.getAcademicRecords(studentId));
            model.addAttribute("gpa", academicService.calculateGPA(studentId));
            model.addAttribute("totalCredits", academicService.totalCredits(studentId));
        }
        return "student-results";
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
        return "redirect:/academic/results?studentId=" + user.getLinkedStudentId();
    }
}
