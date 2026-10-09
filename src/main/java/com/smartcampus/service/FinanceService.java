package com.smartcampus.service;

import com.smartcampus.model.Payment;
import com.smartcampus.model.Student;
import com.smartcampus.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceService {

    private final PaymentRepository repository;
    private final StudentService studentService;
    private final FinanceManager financeManager;

    public FinanceService(PaymentRepository repository, StudentService studentService, FinanceManager financeManager) {
        this.repository = repository;
        this.studentService = studentService;
        this.financeManager = financeManager;
    }

    public double calculateTuitionFee(Long studentId) {
        Student student = studentService.getStudent(studentId);
        return financeManager.calculateTuitionFee(student);
    }

    public Payment recordPayment(Long studentId, double amount, LocalDate paymentDate) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be greater than 0");
        studentService.getStudent(studentId); // throws if the student doesn't exist
        Payment payment = new Payment(studentId, amount, paymentDate != null ? paymentDate : LocalDate.now());
        return repository.save(payment);
    }

    public List<Payment> getPayments(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    public double getOutstandingBalance(Long studentId) {
        Student student = studentService.getStudent(studentId);
        List<Payment> payments = repository.findByStudentId(studentId);
        return financeManager.getOutstandingBalance(student, payments);
    }
}
