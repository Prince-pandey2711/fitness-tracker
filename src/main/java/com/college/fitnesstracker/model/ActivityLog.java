package com.college.fitnesstracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action; // e.g., "USER_LOGIN", "WORKOUT_LOGGED", "CHALLENGE_JOINED", "SETTINGS_UPDATED", etc.

    @Column(length = 1000)
    private String details;

    private String type; // "USER", "ADMIN", "SYSTEM"

    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ActivityLog(String userEmail, String action, String details, String type) {
        this.timestamp = LocalDateTime.now();
        this.userEmail = userEmail;
        this.action = action;
        this.details = details;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
