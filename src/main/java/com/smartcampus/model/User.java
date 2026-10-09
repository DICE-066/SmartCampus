package com.smartcampus.model;

import jakarta.persistence.*;

/** SDD attributes: userId, username, passwordHash, role, linkedStudentId. */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // ADMIN | FINANCE_OFFICER | STUDENT

    private Long linkedStudentId;

    public User() {}

    public User(String username, String passwordHash, String role, Long linkedStudentId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.linkedStudentId = linkedStudentId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getLinkedStudentId() { return linkedStudentId; }
    public void setLinkedStudentId(Long linkedStudentId) { this.linkedStudentId = linkedStudentId; }
}
