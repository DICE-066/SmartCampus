package com.smartcampus.service;

import com.smartcampus.model.AcademicRecord;
import com.smartcampus.model.Payment;
import com.smartcampus.model.Student;
import com.smartcampus.repository.AcademicRecordRepository;
import com.smartcampus.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final StudentService studentService;
    private final AcademicRecordRepository academicRecordRepository;
    private final PaymentRepository paymentRepository;
    private final ReportGenerator reportGenerator;

    public ReportService(StudentService studentService, AcademicRecordRepository academicRecordRepository,
                          PaymentRepository paymentRepository, ReportGenerator reportGenerator) {
        this.studentService = studentService;
        this.academicRecordRepository = academicRecordRepository;
        this.paymentRepository = paymentRepository;
        this.reportGenerator = reportGenerator;
    }

    public String generateStudentReport() {
        List<String> headers = List.of("studentId", "studentNumber", "fullName", "programme", "yearOfStudy", "status");
        List<List<String>> rows = new ArrayList<>();
        for (Student s : studentService.getAllStudents()) {
            rows.add(List.of(
                    String.valueOf(s.getStudentId()), s.getStudentNumber(), s.getFullName(),
                    s.getProgramme(), String.valueOf(s.getYearOfStudy()), s.getStatus()));
        }
        return reportGenerator.exportCSV(headers, rows);
    }

    public String generateAcademicReport() {
        List<String> headers = List.of("recordId", "studentId", "studentNumber", "courseName", "credits", "grade");
        List<List<String>> rows = new ArrayList<>();
        for (AcademicRecord r : academicRecordRepository.findAll()) {
            String studentNumber;
            try {
                studentNumber = studentService.getStudent(r.getStudentId()).getStudentNumber();
            } catch (Exception e) {
                studentNumber = "(unknown)";
            }
            rows.add(List.of(
                    String.valueOf(r.getRecordId()), String.valueOf(r.getStudentId()), studentNumber,
                    r.getCourseName(), String.valueOf(r.getCredits()), String.valueOf(r.getGrade())));
        }
        return reportGenerator.exportCSV(headers, rows);
    }

    public String generateFinancialReport() {
        List<String> headers = List.of("paymentId", "studentId", "studentNumber", "amount", "paymentDate");
        List<List<String>> rows = new ArrayList<>();
        for (Payment p : paymentRepository.findAll()) {
            String studentNumber;
            try {
                studentNumber = studentService.getStudent(p.getStudentId()).getStudentNumber();
            } catch (Exception e) {
                studentNumber = "(unknown)";
            }
            rows.add(List.of(
                    String.valueOf(p.getPaymentId()), String.valueOf(p.getStudentId()), studentNumber,
                    String.valueOf(p.getAmount()), String.valueOf(p.getPaymentDate())));
        }
        return reportGenerator.exportCSV(headers, rows);
    }
}
