package com.college.fitnesstracker.controller;

import com.college.fitnesstracker.model.*;
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

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final FitnessContentService fitnessContentService;
    private final SystemSettingService systemSettingService;
    private final ChallengeService challengeService;
    private final ActivityService activityService;

    public AdminDashboardController(UserService userService,
                                    WorkoutService workoutService,
                                    FitnessContentService fitnessContentService,
                                    SystemSettingService systemSettingService,
                                    ChallengeService challengeService,
                                    ActivityService activityService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.fitnessContentService = fitnessContentService;
        this.systemSettingService = systemSettingService;
        this.challengeService = challengeService;
        this.activityService = activityService;
    }

    private User getSessionAdmin(HttpSession session) {
        User u = (User) session.getAttribute("currentUser");
        if (u != null && u.isAdmin()) {
            return u;
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(@RequestParam(value = "tab", defaultValue = "users") String activeTab,
                                     @RequestParam(value = "success", required = false) String successMsg,
                                     @RequestParam(value = "error", required = false) String errorMsg,
                                     HttpSession session,
                                     Model model) {
        User admin = getSessionAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        // Summary KPI Metrics
        long totalUsers = userService.getTotalUserCount();
        long regularUsers = userService.getRegularUserCount();
        long adminUsers = userService.getAdminUserCount();
        long totalWorkouts = workoutService.getTotalWorkoutsCount();
        int totalMinutes = workoutService.getTotalMinutesLogged();
        int totalCalories = workoutService.getTotalCaloriesLogged();
        long pendingContent = fitnessContentService.getPendingCount();
        long approvedContent = fitnessContentService.getApprovedCount();
        long totalParticipants = challengeService.getTotalParticipantsCount();

        // 1. User Management List
        List<User> usersList = userService.getAllUsers();

        // 2. Fitness Content List
        List<FitnessContent> allContent = fitnessContentService.getAllContentForAdmin();

        // 3. System Settings
        List<SystemSetting> settingsList = systemSettingService.getAllSettings();

        // 4. Fitness Statistics
        Map<String, Object> statsData = workoutService.getAdminStatisticsData();
        List<Challenge> challengesList = challengeService.getAllChallenges();

        // 5. System Activity Monitoring (Real-time Audit Log)
        List<ActivityLog> recentActivities = activityService.getRecentActivities();

        String appName = systemSettingService.getSettingValue("app_name", "FitTrack Pro");

        model.addAttribute("admin", admin);
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("appName", appName);
        model.addAttribute("successMsg", successMsg);
        model.addAttribute("errorMsg", errorMsg);

        // Model attributes
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("regularUsers", regularUsers);
        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("totalWorkouts", totalWorkouts);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("pendingContent", pendingContent);
        model.addAttribute("approvedContent", approvedContent);
        model.addAttribute("totalParticipants", totalParticipants);

        model.addAttribute("usersList", usersList);
        model.addAttribute("allContent", allContent);
        model.addAttribute("settingsList", settingsList);
        model.addAttribute("statsData", statsData);
        model.addAttribute("challengesList", challengesList);
        model.addAttribute("recentActivities", recentActivities);

        return "admin-dashboard";
    }

    // --- 1. User Management Actions ---
    @PostMapping("/users/create")
    public String createUser(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("role") String role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            userService.createUserByAdmin(name, email, password, role, admin.getEmail());
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("success", "User account created successfully for: " + email);
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("error", "Failed to create user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("role") String role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            userService.adminUpdateUser(id, name, email, role, admin.getEmail());
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("success", "User account updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("error", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        if (admin.getId().equals(id)) {
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("error", "You cannot delete your own active administrator account.");
            return "redirect:/admin/dashboard";
        }

        try {
            userService.deleteUser(id, admin.getEmail());
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("success", "User account deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "users");
            redirectAttributes.addAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // --- 2. Fitness Content Management Actions ---
    @PostMapping("/content/approve/{id}")
    public String approveContent(@PathVariable("id") Long id,
                                 @RequestParam(value = "adminNotes", required = false) String adminNotes,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            FitnessContent content = fitnessContentService.approveContent(id, adminNotes, admin.getEmail());
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("success", "Fitness content approved successfully: \"" + content.getTitle() + "\" is now live.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("error", "Error approving content: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/content/reject/{id}")
    public String rejectContent(@PathVariable("id") Long id,
                                @RequestParam(value = "adminNotes", required = false) String adminNotes,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            FitnessContent content = fitnessContentService.rejectContent(id, adminNotes, admin.getEmail());
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("success", "Fitness content rejected: \"" + content.getTitle() + "\" has been marked as rejected.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "content");
            redirectAttributes.addAttribute("error", "Error rejecting content: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // --- 3. System Settings Actions ---
    @PostMapping("/settings/update")
    public String updateSystemSettings(@RequestParam Map<String, String> allParams,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            // Filter non-setting params
            allParams.remove("tab");
            allParams.remove("_csrf");
            systemSettingService.updateMultipleSettings(allParams, admin.getEmail());
            redirectAttributes.addAttribute("tab", "settings");
            redirectAttributes.addAttribute("success", "System settings updated successfully! Configuration changes are active.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "settings");
            redirectAttributes.addAttribute("error", "Error updating settings: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // --- 4. Challenges Management by Admin ---
    @PostMapping("/challenges/create")
    public String createChallenge(@RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("category") String category,
                                  @RequestParam(value = "targetMinutes", defaultValue = "300") Integer targetMinutes,
                                  @RequestParam(value = "targetWorkouts", defaultValue = "10") Integer targetWorkouts,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  @RequestParam(value = "status", defaultValue = "ACTIVE") String status,
                                  @RequestParam("badgeReward") String badgeReward,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            challengeService.createChallenge(title, description, category, targetMinutes, targetWorkouts, startDate, endDate, status, badgeReward, admin.getEmail());
            redirectAttributes.addAttribute("tab", "stats");
            redirectAttributes.addAttribute("success", "New challenge created successfully: " + title);
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "stats");
            redirectAttributes.addAttribute("error", "Error creating challenge: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/challenges/delete/{id}")
    public String deleteChallenge(@PathVariable("id") Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User admin = getSessionAdmin(session);
        if (admin == null) return "redirect:/login";

        try {
            challengeService.deleteChallenge(id, admin.getEmail());
            redirectAttributes.addAttribute("tab", "stats");
            redirectAttributes.addAttribute("success", "Challenge deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("tab", "stats");
            redirectAttributes.addAttribute("error", "Error deleting challenge: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
