package com.college.fitnesstracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type; // Running, Cycling, Gym, Yoga, Swimming, HIIT, Walking

    @Column(nullable = false)
    private Integer duration; // in minutes

    @Column(nullable = false)
    private String intensity; // LOW, MEDIUM, HIGH

    private Integer caloriesBurned;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDate workoutDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Workout() {
        this.workoutDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Workout(User user, String type, Integer duration, String intensity, Integer caloriesBurned, String notes, LocalDate workoutDate) {
        this.user = user;
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
        this.notes = notes;
        this.workoutDate = workoutDate != null ? workoutDate : LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
