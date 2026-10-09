package com.smartcampus.controller;

import com.smartcampus.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ADMIN or FINANCE_OFFICER only (SecurityConfig).
    @GetMapping
    public String landing() {
        return "reports";
    }

    @GetMapping("/students/csv")
    public ResponseEntity<String> studentReport() {
        return csvResponse(reportService.generateStudentReport(), "student-report.csv");
    }

    @GetMapping("/academic/csv")
    public ResponseEntity<String> academicReport() {
        return csvResponse(reportService.generateAcademicReport(), "academic-report.csv");
    }

    @GetMapping("/financial/csv")
    public ResponseEntity<String> financialReport() {
        return csvResponse(reportService.generateFinancialReport(), "financial-report.csv");
    }

    private ResponseEntity<String> csvResponse(String csv, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csv);
    }
}
