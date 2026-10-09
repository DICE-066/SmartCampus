package com.smartcampus.service;

import com.smartcampus.model.AcademicRecord;
import com.smartcampus.repository.AcademicRecordRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AcademicService {

    private final AcademicRecordRepository repository;

    public AcademicService(AcademicRecordRepository repository) {
        this.repository = repository;
    }

    public AcademicRecord addAcademicRecord(AcademicRecord record) {
        validate(record);
        record.setRecordId(null);
        return repository.save(record);
    }

    public AcademicRecord updateAcademicRecord(Long recordId, AcademicRecord updated) {
        AcademicRecord existing = repository.findById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Record " + recordId + " not found"));
        validate(updated);
        existing.setCourseName(updated.getCourseName());
        existing.setCredits(updated.getCredits());
        existing.setGrade(updated.getGrade());
        return repository.save(existing);
    }

    public List<AcademicRecord> getAcademicRecords(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    public double calculateGPA(Long studentId) {
        List<AcademicRecord> records = repository.findByStudentId(studentId);
        int totalCredits = 0;
        double totalPoints = 0;
        for (AcademicRecord r : records) {
            totalCredits += r.getCredits();
            totalPoints += getGradePoint(r.getGrade()) * r.getCredits();
        }
        if (totalCredits == 0) return 0.0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    public int totalCredits(Long studentId) {
        return repository.findByStudentId(studentId).stream().mapToInt(AcademicRecord::getCredits).sum();
    }

    /** Mark -> 4.0 scale. CHECK THIS AGAINST THE SDD and adjust if it differs. */
    public double getGradePoint(double grade) {
        if (grade >= 80) return 4.0;
        if (grade >= 70) return 3.0;
        if (grade >= 60) return 2.0;
        if (grade >= 50) return 1.0;
        return 0.0;
    }

    private void validate(AcademicRecord r) {
        if (r.getStudentId() == null) throw new IllegalArgumentException("studentId is required");
        if (r.getCourseName() == null || r.getCourseName().isBlank())
            throw new IllegalArgumentException("courseName is required");
        if (r.getCredits() <= 0) throw new IllegalArgumentException("credits must be greater than 0");
        if (r.getGrade() < 0 || r.getGrade() > 100)
            throw new IllegalArgumentException("grade must be between 0 and 100");
    }
}
