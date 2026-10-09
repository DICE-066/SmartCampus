package com.smartcampus.service;

import com.smartcampus.model.Student;
import com.smartcampus.repository.StudentRepository;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * SDD: SearchManager — search by student number (exact match first, then partial),
 * kept separate from StudentService so search behaviour can grow independently.
 */
@Component
public class SearchManager {

    private final StudentRepository repository;

    public SearchManager(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> searchByStudentNumber(String query) {
        if (query == null || query.isBlank()) return List.of();

        List<Student> results = new ArrayList<>();
        repository.findByStudentNumber(query).ifPresent(results::add);

        if (results.isEmpty()) {
            for (Student s : repository.findAll()) {
                if (s.getStudentNumber() != null && s.getStudentNumber().contains(query)) {
                    results.add(s);
                }
            }
        }
        return results;
    }
}
