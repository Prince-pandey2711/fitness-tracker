package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.FitnessContent;
import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.repository.FitnessContentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FitnessContentService {

    private final FitnessContentRepository fitnessContentRepository;
    private final ActivityService activityService;

    public FitnessContentService(FitnessContentRepository fitnessContentRepository, ActivityService activityService) {
        this.fitnessContentRepository = fitnessContentRepository;
        this.activityService = activityService;
    }

    public FitnessContent submitContent(User author, String title, String category, String body) {
        FitnessContent content = new FitnessContent(author, title, category, body);
        FitnessContent saved = fitnessContentRepository.save(content);
        activityService.log(author.getEmail(), "CONTENT_SUBMITTED", "Submitted fitness guide: " + title + " (Pending Review)", "USER");
        return saved;
    }

    public FitnessContent approveContent(Long contentId, String adminFeedback, String adminEmail) {
        FitnessContent content = fitnessContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        content.setStatus("APPROVED");
        content.setAdminNotes(adminFeedback != null ? adminFeedback : "Approved by administrator");
        content.setReviewedAt(LocalDateTime.now());
        FitnessContent saved = fitnessContentRepository.save(content);

        activityService.log(adminEmail, "CONTENT_APPROVED", "Approved fitness article: " + content.getTitle(), "ADMIN");
        return saved;
    }

    public FitnessContent rejectContent(Long contentId, String adminFeedback, String adminEmail) {
        FitnessContent content = fitnessContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        content.setStatus("REJECTED");
        content.setAdminNotes(adminFeedback != null ? adminFeedback : "Rejected due to editorial standards");
        content.setReviewedAt(LocalDateTime.now());
        FitnessContent saved = fitnessContentRepository.save(content);

        activityService.log(adminEmail, "CONTENT_REJECTED", "Rejected fitness article: " + content.getTitle(), "ADMIN");
        return saved;
    }

    public List<FitnessContent> getApprovedContent() {
        return fitnessContentRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
    }

    public List<FitnessContent> getAllContentForAdmin() {
        return fitnessContentRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<FitnessContent> getContentByAuthor(User author) {
        return fitnessContentRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public Optional<FitnessContent> getContentById(Long id) {
        return fitnessContentRepository.findById(id);
    }

    public long getPendingCount() {
        return fitnessContentRepository.countByStatus("PENDING");
    }

    public long getApprovedCount() {
        return fitnessContentRepository.countByStatus("APPROVED");
    }
}
