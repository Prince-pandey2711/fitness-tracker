package com.college.fitnesstracker.repository;

import com.college.fitnesstracker.model.Challenge;
import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUserOrderByJoinedAtDesc(User user);
    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);
    boolean existsByUserAndChallenge(User user, Challenge challenge);
    List<UserChallenge> findByChallenge(Challenge challenge);
    long countByChallenge(Challenge challenge);
}
