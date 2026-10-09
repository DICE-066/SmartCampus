package com.smartcampus.service;

import com.smartcampus.model.Payment;
import com.smartcampus.model.Student;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * SDD: FinanceManager — holds the fee/balance calculation logic, kept
 * separate from FinanceService (which owns persistence/CRUD), mirroring
 * the StudentService/SearchManager split in the Student module.
 *
 * ASSUMPTION — no fee schedule was given in the SRS/SDD excerpts seen so
 * far: tuition is a flat amount per year of study. Replace FLAT_FEE_PER_YEAR
 * with your actual fee table (e.g. per programme/faculty) once you have it.
 */
@Component
public class FinanceManager {

    private static final double FLAT_FEE_PER_YEAR = 45000.0;

    public double calculateTuitionFee(Student student) {
        if (student == null) return 0.0;
        return FLAT_FEE_PER_YEAR;
    }

    /** Outstanding Balance = Tuition Fee - Total Payments (per the plan's own formula). */
    public double getOutstandingBalance(Student student, List<Payment> payments) {
        double tuitionFee = calculateTuitionFee(student);
        double totalPaid = payments.stream().mapToDouble(Payment::getAmount).sum();
        return Math.round((tuitionFee - totalPaid) * 100.0) / 100.0;
    }
}
