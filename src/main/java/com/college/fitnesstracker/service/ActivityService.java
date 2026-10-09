package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.ActivityLog;
import com.college.fitnesstracker.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void log(String userEmail, String action, String details, String type) {
        ActivityLog log = new ActivityLog(userEmail, action, details, type);
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
