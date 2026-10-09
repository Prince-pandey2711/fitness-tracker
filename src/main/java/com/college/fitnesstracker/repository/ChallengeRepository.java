package com.college.fitnesstracker.repository;

import com.college.fitnesstracker.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByStatusOrderByStartDateDesc(String status);
    List<Challenge> findAllByOrderByStartDateDesc();
}
