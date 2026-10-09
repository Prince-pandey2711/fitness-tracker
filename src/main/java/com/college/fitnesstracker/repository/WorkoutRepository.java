package com.college.fitnesstracker.repository;

import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserOrderByWorkoutDateDescCreatedAtDesc(User user);
    long countByUser(User user);
    List<Workout> findAllByOrderByWorkoutDateDescCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(w.duration), 0) FROM Workout w WHERE w.user = :user")
    Integer getTotalDurationMinutesByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(w.caloriesBurned), 0) FROM Workout w WHERE w.user = :user")
    Integer getTotalCaloriesBurnedByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(w.duration), 0) FROM Workout w")
    Integer getTotalSystemWorkoutMinutes();

    @Query("SELECT COALESCE(SUM(w.caloriesBurned), 0) FROM Workout w")
    Integer getTotalSystemCalories();
}
