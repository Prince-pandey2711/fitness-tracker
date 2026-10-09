package com.college.fitnesstracker.repository;

import com.college.fitnesstracker.model.FitnessContent;
import com.college.fitnesstracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessContentRepository extends JpaRepository<FitnessContent, Long> {
    List<FitnessContent> findByStatusOrderByCreatedAtDesc(String status);
    List<FitnessContent> findByAuthorOrderByCreatedAtDesc(User author);
    List<FitnessContent> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
