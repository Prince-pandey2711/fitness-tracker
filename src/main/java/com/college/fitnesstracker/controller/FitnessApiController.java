package com.college.fitnesstracker.controller;

import com.college.fitnesstracker.model.ActivityLog;
import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.service.ActivityService;
import com.college.fitnesstracker.service.WorkoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FitnessApiController {

    private final ActivityService activityService;
    private final WorkoutService workoutService;

    public FitnessApiController(ActivityService activityService, WorkoutService workoutService) {
        this.activityService = activityService;
        this.workoutService = workoutService;
    }

    @GetMapping("/activities/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivities(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(activityService.getRecentActivities());
    }

    @GetMapping("/estimate-calories")
    public ResponseEntity<Map<String, Object>> estimateCalories(@RequestParam("type") String type,
                                                                @RequestParam("duration") int duration,
                                                                @RequestParam("intensity") String intensity,
                                                                HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        Double weight = (currentUser != null && currentUser.getWeight() != null) ? currentUser.getWeight() : 70.0;
        int calories = WorkoutService.estimateCalories(type, duration, intensity, weight);

        Map<String, Object> resp = new HashMap<>();
        resp.put("type", type);
        resp.put("duration", duration);
        resp.put("intensity", intensity);
        resp.put("estimatedCalories", calories);
        return ResponseEntity.ok(resp);
    }
}
