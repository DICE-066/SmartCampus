package com.smartcampus.model;

import jakarta.persistence.*;

/** Attributes per the approved SDD: studentId, studentNumber, fullName, programme, yearOfStudy, status. */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(nullable = false, unique = true)
    private String studentNumber;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String programme;

    @Column(nullable = false)
    private int yearOfStudy;

    @Column(nullable = false)
    private String status = "active"; // active | graduated | deregistered

    public Student() {}

    public Student(String studentNumber, String fullName, String programme, int yearOfStudy) {
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.programme = programme;
        this.yearOfStudy = yearOfStudy;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
