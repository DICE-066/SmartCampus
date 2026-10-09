package com.smartcampus.service;

import com.smartcampus.model.Student;
import com.smartcampus.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final SearchManager searchManager;

    public StudentService(StudentRepository repository, SearchManager searchManager) {
        this.repository = repository;
        this.searchManager = searchManager;
    }

    public Student registerStudent(Student student) {
        validate(student);
        if (repository.existsByStudentNumber(student.getStudentNumber()))
            throw new IllegalArgumentException("A student with this student number already exists");
        student.setStudentId(null);
        return repository.save(student);
    }

    public Student updateStudent(Long studentId, Student updated) {
        Student existing = getStudent(studentId);
        validate(updated);
        existing.setStudentNumber(updated.getStudentNumber());
        existing.setFullName(updated.getFullName());
        existing.setProgramme(updated.getProgramme());
        existing.setYearOfStudy(updated.getYearOfStudy());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        return repository.save(existing);
    }

    public void deleteStudent(Long studentId) {
        if (!repository.existsById(studentId))
            throw new NoSuchElementException("Student " + studentId + " not found");
        repository.deleteById(studentId);
    }

    public Student getStudent(Long studentId) {
        return repository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student " + studentId + " not found"));
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public List<Student> searchStudent(String query) {
        return searchManager.searchByStudentNumber(query);
    }

    private void validate(Student s) {
        if (s.getStudentNumber() == null || s.getStudentNumber().isBlank())
            throw new IllegalArgumentException("studentNumber is required");
        if (s.getFullName() == null || s.getFullName().isBlank())
            throw new IllegalArgumentException("fullName is required");
        if (s.getProgramme() == null || s.getProgramme().isBlank())
            throw new IllegalArgumentException("programme is required");
        if (s.getYearOfStudy() < 1 || s.getYearOfStudy() > 6)
            throw new IllegalArgumentException("yearOfStudy must be between 1 and 6");
    }
}
