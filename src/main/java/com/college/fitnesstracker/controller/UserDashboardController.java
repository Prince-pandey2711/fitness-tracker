package com.college.fitnesstracker.controller;

import com.college.fitnesstracker.model.Challenge;
import com.college.fitnesstracker.model.FitnessContent;
import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.UserChallenge;
import com.college.fitnesstracker.model.Workout;
import com.college.fitnesstracker.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final ChallengeService challengeService;
    private final FitnessContentService fitnessContentService;
    private final FitnessGuidanceService fitnessGuidanceService;
    private final SystemSettingService systemSettingService;

    public UserDashboardController(UserService userService,
                                  WorkoutService workoutService,
                                  ChallengeService challengeService,
                                  FitnessContentService fitnessContentService,
                                  FitnessGuidanceService fitnessGuidanceService,
                                  SystemSettingService systemSettingService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.challengeService = challengeService;
        this.fitnessContentService = fitnessContentService;
        this.fitnessGuidanceService = fitnessGuidanceService;
        this.systemSettingService = systemSettingService;
    }

    private User getSessionUser(HttpSession session) {
        User u = (User) session.getAttribute("currentUser");
        if (u != null) {
            // refresh user from database to ensure fresh data
            return userService.findById(u.getId()).orElse(u);
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(value = "tab", defaultValue = "workouts") String activeTab,
                                @RequestParam(value = "success", required = false) String successMsg,
                                @RequestParam(value = "error", required = false) String errorMsg,
                                HttpSession session,
                                Model model) {
        User user = getSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        // update session with fresh user state
        session.setAttribute("currentUser", user);

        List<Workout> workouts = workoutService.getWorkoutsByUser(user);
        Map<String, Object> progress = workoutService.getUserProgressData(user);
        List<Challenge> activeChallenges = challengeService.getActiveChallenges();
        List<UserChallenge> userActiveParticipations = challengeService.getUserActiveParticipations(user);
        List<UserChallenge> challengeHistory = challengeService.getUserChallengeHistory(user);
        Set<Long> joinedChallengeIds = challengeService.getJoinedChallengeIds(user);
        List<FitnessContent> approvedContent = fitnessContentService.getApprovedContent();
        List<FitnessContent> userSubmissions = fitnessContentService.getContentByAuthor(user);
        Map<String, Object> guidance = fitnessGuidanceService.generateGuidance(user, workouts);

        String appName = systemSettingService.getSettingValue("app_name", "FitTrack Pro");
        String announcement = systemSettingService.getSettingValue("system_announcement", "");

        model.addAttribute("user", user);
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("appName", appName);
        model.addAttribute("announcement", announcement);
        model.addAttribute("successMsg", successMsg);
        model.addAttribute("errorMsg", errorMsg);

        // Core Dashboard data
        model.addAttribute("workouts", workouts);
        model.addAttribute("progress", progress);
        model.addAttribute("activeChallenges", activeChallenges);
        model.addAttribute("userActiveParticipations", userActiveParticipations);
        model.addAttribute("challengeHistory", challengeHistory);
        model.addAttribute("joinedChallengeIds", joinedChallengeIds);
        model.addAttribute("approvedContent", approvedContent);
        model.addAttribute("userSubmissions", userSubmissions);
        model.addAttribute("guidance", guidance);

        return "user-dashboard";
    }

    // --- 1. Workout Logging Endpoints ---
    @PostMapping("/workouts/add")
    public String logWorkout(@RequestParam("type") String type,
                             @RequestParam("duration") Integer duration,
                             @RequestParam("intensity") String intensity,
                             @RequestParam(value = "caloriesBurned", required = false) Integer caloriesBurned,
                             @RequestParam(value = "notes", required = false) String notes,
                             @RequestParam(value = "workoutDate", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workoutDate,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        if (workoutDate == null) {
            workoutDate = LocalDate.now();
        }

        Workout saved = workoutService.logWorkout(user, type, duration, intensity, caloriesBurned, notes, workoutDate);
        redirectAttributes.addAttribute("tab", "workouts");
        redirectAttributes.addAttribute("success", "Workout logged successfully! (" + saved.getType() + " - " + saved.getDuration() + " mins, " + saved.getCaloriesBurned() + " kcal)");
        return "redirect:/user/dashboard";
    }

    @PostMapping("/workouts/edit/{id}")
    public String editWorkout(@PathVariable("id") Long id,
                              @RequestParam("type") String type,
                              @RequestParam("duration") Integer duration,
                              @RequestParam("intensity") String intensity,
                              @RequestParam(value = "caloriesBurned", required = false) Integer caloriesBurned,
                              @RequestParam(value = "notes", required = false) String notes,
                              @RequestParam(value = "workoutDate", required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workoutDate,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            workoutService.updateWorkout(id, user, type, duration, intensity, caloriesBurned, notes, workoutDate);
            redirectAttributes.addAttribute("tab", "workouts");
            redirectAttributes.addAttribute("success", "Workout log updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "workouts");
            redirectAttributes.addAttribute("error", "Error updating workout: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    @PostMapping("/workouts/delete/{id}")
    public String deleteWorkout(@PathVariable("id") Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            workoutService.deleteWorkout(id, user);
            redirectAttributes.addAttribute("tab", "workouts");
            redirectAttributes.addAttribute("success", "Workout log deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "workouts");
            redirectAttributes.addAttribute("error", "Error deleting workout: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    // --- 2. Fitness Challenges Endpoints ---
    @PostMapping("/challenges/join/{id}")
    public String joinChallenge(@PathVariable("id") Long challengeId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            UserChallenge participation = challengeService.joinChallenge(user, challengeId);
            redirectAttributes.addAttribute("tab", "challenges");
            redirectAttributes.addAttribute("success", "Awesome! Successfully joined challenge: " + participation.getChallenge().getTitle());
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "challenges");
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    @PostMapping("/challenges/update-progress/{participationId}")
    public String updateChallengeProgress(@PathVariable("participationId") Long participationId,
                                          @RequestParam("progress") Integer progress,
                                          @RequestParam(value = "completed", defaultValue = "false") boolean completed,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            challengeService.updateProgress(participationId, progress, completed, user);
            redirectAttributes.addAttribute("tab", "challenges");
            redirectAttributes.addAttribute("success", "Challenge progress updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "challenges");
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    // --- 3. Profile Management Endpoints ---
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam(value = "fitnessGoal", required = false) String fitnessGoal,
                                @RequestParam(value = "weight", required = false) Double weight,
                                @RequestParam(value = "height", required = false) Double height,
                                @RequestParam(value = "targetMinutesPerWeek", required = false) Integer targetMinutesPerWeek,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            User updated = userService.updateProfile(user.getId(), name, email, password, fitnessGoal, weight, height, targetMinutesPerWeek);
            session.setAttribute("currentUser", updated);
            redirectAttributes.addAttribute("tab", "profile");
            redirectAttributes.addAttribute("success", "Profile updated successfully! All fitness metrics refreshed.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "profile");
            redirectAttributes.addAttribute("error", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    // --- 4. Content Contribution Endpoints ---
    @PostMapping("/content/submit")
    public String submitFitnessContent(@RequestParam("title") String title,
                                       @RequestParam("category") String category,
                                       @RequestParam("body") String body,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        try {
            fitnessContentService.submitContent(user, title, category, body);
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("success", "Fitness content submitted successfully! It will appear publicly once approved by an administrator.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("error", "Error submitting content: " + e.getMessage());
        }
        return "redirect:/user/dashboard";
    }
}
