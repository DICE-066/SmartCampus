package com.smartcampus.controller;

import com.smartcampus.model.Student;
import com.smartcampus.model.User;
import com.smartcampus.service.AuthService;
import com.smartcampus.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AuthService authService;
    private final StudentService studentService;

    public ProfileController(AuthService authService, StudentService studentService) {
        this.authService = authService;
        this.studentService = studentService;
    }

    @GetMapping
    public String profile(Authentication authentication, Model model) {
        User user = authService.currentUser(authentication);
        model.addAttribute("user", user);

        if (user != null && user.getLinkedStudentId() != null) {
            try {
                Student linked = studentService.getStudent(user.getLinkedStudentId());
                model.addAttribute("linkedStudent", linked);
            } catch (Exception ignored) {
                // linked id points at a student that no longer exists — just show nothing
            }
        }
        return "profile";
    }

    @PostMapping("/password")
    public String changePassword(Authentication authentication,
                                  @RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirect) {
        User user = authService.currentUser(authentication);
        if (!newPassword.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "New password and confirmation don't match.");
            return "redirect:/profile";
        }
        try {
            authService.changePassword(user, currentPassword, newPassword);
            redirect.addFlashAttribute("message", "Password updated.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
