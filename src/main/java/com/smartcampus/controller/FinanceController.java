package com.smartcampus.controller;

import com.smartcampus.model.User;
import com.smartcampus.service.AuthService;
import com.smartcampus.service.FinanceService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final AuthService authService;

    public FinanceController(FinanceService financeService, AuthService authService) {
        this.financeService = financeService;
        this.authService = authService;
    }

    // ADMIN or FINANCE_OFFICER: generic lookup landing page.
    @GetMapping
    public String landing() {
        return "finance";
    }

    // ADMIN or FINANCE_OFFICER only (SecurityConfig).
    @GetMapping("/payment-form")
    public String paymentForm(@RequestParam(required = false) Long studentId, Model model) {
        model.addAttribute("studentId", studentId);
        return "payment-form";
    }

    // ADMIN or FINANCE_OFFICER only.
    @PostMapping("/record")
    public String record(@RequestParam Long studentId, @RequestParam double amount,
                          @RequestParam(required = false) String paymentDate, RedirectAttributes redirect) {
        try {
            LocalDate date = (paymentDate != null && !paymentDate.isBlank()) ? LocalDate.parse(paymentDate) : null;
            financeService.recordPayment(studentId, amount, date);
            redirect.addFlashAttribute("message", "Payment recorded.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/finance/payment-form?studentId=" + studentId;
        }
        return "redirect:/finance/history?studentId=" + studentId;
    }

    // Reachable by ADMIN, FINANCE_OFFICER, or STUDENT — ownership enforced here,
    // same pattern as StudentController/AcademicController.
    @GetMapping("/history")
    public String history(@RequestParam(required = false) Long studentId, Model model, Authentication authentication) {
        if (studentId != null) {
            if (!authService.canAccessStudent(authentication, studentId) && !authService.isFinanceOfficer(authentication)) {
                throw new AccessDeniedException("Not permitted to view this student's financial records.");
            }
            model.addAttribute("studentId", studentId);
            model.addAttribute("payments", financeService.getPayments(studentId));
            model.addAttribute("tuitionFee", financeService.calculateTuitionFee(studentId));
            model.addAttribute("balance", financeService.getOutstandingBalance(studentId));
        }
        return "payment-history";
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
        return "redirect:/finance/history?studentId=" + user.getLinkedStudentId();
    }
}
