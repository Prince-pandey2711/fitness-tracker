package com.college.fitnesstracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String category; // "Cardio", "Strength", "Endurance", "Flexibility", "Wellness"

    private Integer targetMinutes; // e.g., 300 minutes total

    private Integer targetWorkouts; // e.g., 10 workout sessions

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // "UPCOMING", "ACTIVE", "COMPLETED"

    private String badgeReward; // e.g., "Cardio Warrior", "30-Day Master", "Iron Lifter"

    public Challenge() {
        this.status = "ACTIVE";
    }

    public Challenge(String title, String description, String category, Integer targetMinutes, Integer targetWorkouts, LocalDate startDate, LocalDate endDate, String status, String badgeReward) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.targetMinutes = targetMinutes;
        this.targetWorkouts = targetWorkouts;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.badgeReward = badgeReward;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTargetMinutes() {
        return targetMinutes;
    }

    public void setTargetMinutes(Integer targetMinutes) {
        this.targetMinutes = targetMinutes;
    }

    public Integer getTargetWorkouts() {
        return targetWorkouts;
    }

    public void setTargetWorkouts(Integer targetWorkouts) {
        this.targetWorkouts = targetWorkouts;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBadgeReward() {
        return badgeReward;
    }

    public void setBadgeReward(String badgeReward) {
        this.badgeReward = badgeReward;
    }
}
