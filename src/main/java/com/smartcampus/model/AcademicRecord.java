package com.smartcampus.model;

import jakarta.persistence.*;

/** Attributes per the SDD: recordId, studentId, courseName, credits, grade. */
@Entity
@Table(name = "academic_records")
public class AcademicRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private double grade;

    public AcademicRecord() {}

    public AcademicRecord(Long studentId, String courseName, int credits, double grade) {
        this.studentId = studentId;
        this.courseName = courseName;
        this.credits = credits;
        this.grade = grade;
    }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
}
