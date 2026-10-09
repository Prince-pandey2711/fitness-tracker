package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.Workout;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FitnessGuidanceService {

    public Map<String, Object> generateGuidance(User user, List<Workout> recentWorkouts) {
        Map<String, Object> guidance = new HashMap<>();

        // 1. BMI Calculation
        double bmi = 0.0;
        String bmiCategory = "Unknown";
        if (user.getWeight() != null && user.getHeight() != null && user.getHeight() > 50) {
            double heightInMeters = user.getHeight() / 100.0;
            bmi = Math.round((user.getWeight() / (heightInMeters * heightInMeters)) * 10.0) / 10.0;
            if (bmi < 18.5) {
                bmiCategory = "Underweight";
            } else if (bmi < 25.0) {
                bmiCategory = "Normal Weight";
            } else if (bmi < 30.0) {
                bmiCategory = "Overweight";
            } else {
                bmiCategory = "Obese";
            }
        }
        guidance.put("bmi", bmi);
        guidance.put("bmiCategory", bmiCategory);

        // 2. Workout statistics
        int totalMinutes = recentWorkouts.stream().mapToInt(Workout::getDuration).sum();
        int totalWorkouts = recentWorkouts.size();
        int targetMinutes = user.getTargetMinutesPerWeek() != null ? user.getTargetMinutesPerWeek() : 150;

        List<String> recommendations = new ArrayList<>();
        List<String> dietTips = new ArrayList<>();

        // Goal-based recommendations
        String goal = user.getFitnessGoal() != null ? user.getFitnessGoal().toLowerCase() : "general health";
        if (goal.contains("weight loss") || goal.contains("fat loss")) {
            recommendations.add("Prioritize 45-60 minutes of mixed moderate-to-high intensity cardio (Running, HIIT, Cycling) 4 times a week.");
            recommendations.add("Incorporate full-body resistance training twice a week to preserve lean muscle mass.");
            dietTips.add("Maintain a sustainable caloric deficit of 300-500 kcal per day with 1.6g protein per kg of body weight.");
            dietTips.add("Stay hydrated with at least 3 liters of water daily and limit refined sugar intake.");
        } else if (goal.contains("muscle") || goal.contains("hypertrophy")) {
            recommendations.add("Focus on progressive overload in compound lifts (Squats, Bench Press, Deadlifts, Pull-ups) 3-4 days a week.");
            recommendations.add("Limit excessive steady-state cardio to 20-minute recovery sessions to avoid blunting muscle growth.");
            dietTips.add("Aim for 1.8g - 2.2g of protein per kg of body weight paired with nutrient-dense complex carbs.");
            dietTips.add("Ensure 7-9 hours of quality sleep nightly for optimal testosterone and growth hormone production.");
        } else if (goal.contains("endurance") || goal.contains("cardio")) {
            recommendations.add("Incorporate long-slow-distance (LSD) Zone 2 aerobic workouts (60-90 mins) to build mitochondrial density.");
            recommendations.add("Add one weekly threshold tempo session and mobility work to keep joints flexible.");
            dietTips.add("Carbohydrate cycling around key training days will optimize muscle glycogen stores.");
            dietTips.add("Replenish electrolytes (sodium, potassium, magnesium) before and after strenuous endurance sessions.");
        } else {
            recommendations.add("World Health Organization recommends at least 150 minutes of moderate activity or 75 minutes of vigorous activity each week.");
            recommendations.add("Mix 30 minutes of brisk walking or jogging with dynamic stretching and basic bodyweight movements.");
            dietTips.add("Eat balanced whole foods comprising 50% vegetables/greens, 25% lean protein, and 25% healthy whole grains.");
            dietTips.add("Practice mindful eating and avoid late-night heavy snacking.");
        }

        // Target pacing advice
        if (totalMinutes >= targetMinutes) {
            guidance.put("statusBadge", "Goal Achiever!");
            guidance.put("statusMessage", "Fantastic work! You've crushed your target of " + targetMinutes + " active minutes this week!");
        } else if (totalMinutes >= targetMinutes / 2) {
            guidance.put("statusBadge", "On Track");
            guidance.put("statusMessage", "Good progress! You have completed " + totalMinutes + " minutes. Just " + (targetMinutes - totalMinutes) + " more minutes to hit your weekly target!");
        } else {
            guidance.put("statusBadge", "Needs Boost");
            guidance.put("statusMessage", "You've logged " + totalMinutes + " minutes so far. Try scheduling a quick 25-minute workout today to stay consistent!");
        }

        guidance.put("recommendations", recommendations);
        dietTips.add("Maintain consistent workout timing to establish an ingrained circadian fitness rhythm.");
        guidance.put("dietTips", dietTips);

        return guidance;
    }
}
