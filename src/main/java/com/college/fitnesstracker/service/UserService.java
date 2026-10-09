package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityService activityService;

    public UserService(UserRepository userRepository, ActivityService activityService) {
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            activityService.log(email, "USER_LOGIN", "User logged in successfully", userOpt.get().isAdmin() ? "ADMIN" : "USER");
            return userOpt;
        }
        return Optional.empty();
    }

    public User register(String name, String email, String password, String role) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email address is already registered.");
        }
        String assignedRole = "admin".equalsIgnoreCase(role) ? "ADMIN" : "USER";
        User user = new User(name.trim(), normalizedEmail, password, assignedRole);
        User saved = userRepository.save(user);
        activityService.log(normalizedEmail, "USER_REGISTERED", "New account registered with role " + assignedRole, "USER");
        return saved;
    }

    public User createUserByAdmin(String name, String email, String password, String role, String adminEmail) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("User with this email already exists.");
        }
        User user = new User(name.trim(), normalizedEmail, password, role.toUpperCase());
        User saved = userRepository.save(user);
        activityService.log(adminEmail, "ADMIN_CREATED_USER", "Admin created user: " + normalizedEmail + " (" + role + ")", "ADMIN");
        return saved;
    }

    public User updateProfile(Long userId, String name, String email, String password, String fitnessGoal, Double weight, Double height, Integer targetMinutes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String normalizedEmail = email.trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email address is already in use by another account.");
        }

        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password.trim());
        }
        if (fitnessGoal != null && !fitnessGoal.trim().isEmpty()) {
            user.setFitnessGoal(fitnessGoal);
        }
        if (weight != null && weight > 0) {
            user.setWeight(weight);
        }
        if (height != null && height > 0) {
            user.setHeight(height);
        }
        if (targetMinutes != null && targetMinutes > 0) {
            user.setTargetMinutesPerWeek(targetMinutes);
        }

        User updated = userRepository.save(user);
        activityService.log(updated.getEmail(), "PROFILE_UPDATED", "Profile updated successfully", "USER");
        return updated;
    }

    public User adminUpdateUser(Long userId, String name, String email, String role, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String normalizedEmail = email.trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email address is already in use by another user.");
        }

        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setRole(role.toUpperCase());

        User updated = userRepository.save(user);
        activityService.log(adminEmail, "ADMIN_UPDATED_USER", "Admin modified account details for: " + normalizedEmail, "ADMIN");
        return updated;
    }

    public void deleteUser(Long userId, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String deletedEmail = user.getEmail();
        userRepository.delete(user);
        activityService.log(adminEmail, "ADMIN_DELETED_USER", "Admin deleted account: " + deletedEmail, "ADMIN");
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public long getRegularUserCount() {
        return userRepository.countByRole("USER");
    }

    public long getAdminUserCount() {
        return userRepository.countByRole("ADMIN");
    }
}
