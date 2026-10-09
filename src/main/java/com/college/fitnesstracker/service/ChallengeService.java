package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.Challenge;
import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.UserChallenge;
import com.college.fitnesstracker.repository.ChallengeRepository;
import com.college.fitnesstracker.repository.UserChallengeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ActivityService activityService;

    public ChallengeService(ChallengeRepository challengeRepository,
                            UserChallengeRepository userChallengeRepository,
                            ActivityService activityService) {
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.activityService = activityService;
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAllByOrderByStartDateDesc();
    }

    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findByStatusOrderByStartDateDesc("ACTIVE");
    }

    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }

    public Challenge createChallenge(String title, String description, String category,
                                    Integer targetMinutes, Integer targetWorkouts,
                                    LocalDate startDate, LocalDate endDate,
                                    String status, String badgeReward, String adminEmail) {
        Challenge challenge = new Challenge(title, description, category, targetMinutes, targetWorkouts, startDate, endDate, status, badgeReward);
        Challenge saved = challengeRepository.save(challenge);
        activityService.log(adminEmail, "CHALLENGE_CREATED", "Admin created new challenge: " + title, "ADMIN");
        return saved;
    }

    public void deleteChallenge(Long id, String adminEmail) {
        challengeRepository.findById(id).ifPresent(c -> {
            String title = c.getTitle();
            challengeRepository.delete(c);
            activityService.log(adminEmail, "CHALLENGE_DELETED", "Admin deleted challenge: " + title, "ADMIN");
        });
    }

    public UserChallenge joinChallenge(User user, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        if (userChallengeRepository.existsByUserAndChallenge(user, challenge)) {
            throw new IllegalArgumentException("You have already joined the challenge: " + challenge.getTitle());
        }

        UserChallenge userChallenge = new UserChallenge(user, challenge);
        userChallenge.setStatus("JOINED");
        userChallenge.setCurrentProgress(10); // initial starter milestone
        UserChallenge saved = userChallengeRepository.save(userChallenge);

        activityService.log(user.getEmail(), "CHALLENGE_JOINED", "User joined fitness challenge: " + challenge.getTitle(), "USER");
        return saved;
    }

    public List<UserChallenge> getUserParticipations(User user) {
        return userChallengeRepository.findByUserOrderByJoinedAtDesc(user);
    }

    public List<UserChallenge> getUserActiveParticipations(User user) {
        return userChallengeRepository.findByUserOrderByJoinedAtDesc(user).stream()
                .filter(uc -> !"COMPLETED".equalsIgnoreCase(uc.getStatus()))
                .collect(Collectors.toList());
    }

    public List<UserChallenge> getUserChallengeHistory(User user) {
        return userChallengeRepository.findByUserOrderByJoinedAtDesc(user).stream()
                .filter(uc -> "COMPLETED".equalsIgnoreCase(uc.getStatus()) || "COMPLETED".equalsIgnoreCase(uc.getChallenge().getStatus()))
                .collect(Collectors.toList());
    }

    public void updateProgress(Long participationId, Integer progressPercent, boolean isCompleted, User user) {
        UserChallenge uc = userChallengeRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participation record not found"));

        if (!uc.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new SecurityException("Unauthorized");
        }

        uc.setCurrentProgress(Math.min(100, Math.max(0, progressPercent)));
        if (isCompleted || uc.getCurrentProgress() >= 100) {
            uc.setStatus("COMPLETED");
            uc.setCompletedAt(LocalDateTime.now());
            uc.setResult("Completed Successfully! Earned Badge: " + uc.getChallenge().getBadgeReward());
            activityService.log(user.getEmail(), "CHALLENGE_COMPLETED", "Completed challenge: " + uc.getChallenge().getTitle(), "USER");
        } else {
            uc.setStatus("IN_PROGRESS");
            uc.setResult("In Progress (" + uc.getCurrentProgress() + "% completed)");
        }
        userChallengeRepository.save(uc);
    }

    public long getTotalParticipantsCount() {
        return userChallengeRepository.count();
    }

    public Set<Long> getJoinedChallengeIds(User user) {
        return userChallengeRepository.findByUserOrderByJoinedAtDesc(user).stream()
                .map(uc -> uc.getChallenge().getId())
                .collect(Collectors.toSet());
    }
}
