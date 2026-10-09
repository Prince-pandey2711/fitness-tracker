package com.college.fitnesstracker.service;

import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.model.Workout;
import com.college.fitnesstracker.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ActivityService activityService;

    public WorkoutService(WorkoutRepository workoutRepository, ActivityService activityService) {
        this.workoutRepository = workoutRepository;
        this.activityService = activityService;
    }

    public Workout logWorkout(User user, String type, Integer duration, String intensity, Integer caloriesBurned, String notes, LocalDate date) {
        if (caloriesBurned == null || caloriesBurned <= 0) {
            caloriesBurned = estimateCalories(type, duration, intensity, user.getWeight());
        }
        Workout workout = new Workout(user, type, duration, intensity.toUpperCase(), caloriesBurned, notes, date);
        Workout saved = workoutRepository.save(workout);
        activityService.log(user.getEmail(), "WORKOUT_LOGGED", "Logged " + duration + " min " + type + " (" + intensity + ")", "USER");
        return saved;
    }

    public Workout updateWorkout(Long id, User user, String type, Integer duration, String intensity, Integer caloriesBurned, String notes, LocalDate date) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        if (!workout.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new SecurityException("Unauthorized to edit this workout log");
        }

        if (caloriesBurned == null || caloriesBurned <= 0) {
            caloriesBurned = estimateCalories(type, duration, intensity, user.getWeight());
        }

        workout.setType(type);
        workout.setDuration(duration);
        workout.setIntensity(intensity.toUpperCase());
        workout.setCaloriesBurned(caloriesBurned);
        workout.setNotes(notes);
        if (date != null) {
            workout.setWorkoutDate(date);
        }

        Workout updated = workoutRepository.save(workout);
        activityService.log(user.getEmail(), "WORKOUT_UPDATED", "Updated workout #" + id + " (" + type + ")", "USER");
        return updated;
    }

    public void deleteWorkout(Long id, User user) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        if (!workout.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new SecurityException("Unauthorized to delete this workout log");
        }

        workoutRepository.delete(workout);
        activityService.log(user.getEmail(), "WORKOUT_DELETED", "Deleted workout log #" + id, "USER");
    }

    public Optional<Workout> getWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    public List<Workout> getWorkoutsByUser(User user) {
        return workoutRepository.findByUserOrderByWorkoutDateDescCreatedAtDesc(user);
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAllByOrderByWorkoutDateDescCreatedAtDesc();
    }

    public long getTotalWorkoutsCount() {
        return workoutRepository.count();
    }

    public int getTotalMinutesLogged() {
        Integer mins = workoutRepository.getTotalSystemWorkoutMinutes();
        return mins != null ? mins : 0;
    }

    public int getTotalCaloriesLogged() {
        Integer cals = workoutRepository.getTotalSystemCalories();
        return cals != null ? cals : 0;
    }

    public int getUserTotalMinutes(User user) {
        Integer mins = workoutRepository.getTotalDurationMinutesByUser(user);
        return mins != null ? mins : 0;
    }

    public int getUserTotalCalories(User user) {
        Integer cals = workoutRepository.getTotalCaloriesBurnedByUser(user);
        return cals != null ? cals : 0;
    }

    public Map<String, Object> getUserProgressData(User user) {
        List<Workout> workouts = getWorkoutsByUser(user);
        Map<String, Object> data = new HashMap<>();

        int totalMins = workouts.stream().mapToInt(Workout::getDuration).sum();
        int totalCals = workouts.stream().mapToInt(w -> w.getCaloriesBurned() != null ? w.getCaloriesBurned() : 0).sum();
        int workoutCount = workouts.size();

        // Calculate progress towards weekly target
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        List<Workout> thisWeekWorkouts = workouts.stream()
                .filter(w -> !w.getWorkoutDate().isBefore(sevenDaysAgo))
                .collect(Collectors.toList());
        int weeklyMinutes = thisWeekWorkouts.stream().mapToInt(Workout::getDuration).sum();
        int targetWeekly = user.getTargetMinutesPerWeek() != null ? user.getTargetMinutesPerWeek() : 150;
        int weeklyPercentage = targetWeekly > 0 ? Math.min(100, (int) Math.round(((double) weeklyMinutes / targetWeekly) * 100)) : 0;

        // Group by workout type
        Map<String, Long> countByType = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getType, Collectors.counting()));

        // Group by intensity
        Map<String, Long> countByIntensity = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getIntensity, Collectors.counting()));

        // Last 7 days minutes for daily trend chart
        Map<String, Integer> last7DaysMinutes = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            String dayKey = day.getDayOfWeek().name().substring(0, 3) + " (" + day.getMonthValue() + "/" + day.getDayOfMonth() + ")";
            int dayMins = workouts.stream()
                    .filter(w -> w.getWorkoutDate().equals(day))
                    .mapToInt(Workout::getDuration)
                    .sum();
            last7DaysMinutes.put(dayKey, dayMins);
        }

        data.put("totalWorkouts", workoutCount);
        data.put("totalMinutes", totalMins);
        data.put("totalCalories", totalCals);
        data.put("weeklyMinutes", weeklyMinutes);
        data.put("targetWeeklyMinutes", targetWeekly);
        data.put("weeklyProgressPercentage", weeklyPercentage);
        data.put("countByType", countByType);
        data.put("countByIntensity", countByIntensity);
        data.put("last7DaysMinutes", last7DaysMinutes);

        return data;
    }

    public Map<String, Object> getAdminStatisticsData() {
        List<Workout> workouts = getAllWorkouts();
        Map<String, Object> data = new HashMap<>();

        Map<String, Long> workoutsByType = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getType, Collectors.counting()));

        Map<String, Long> workoutsByIntensity = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getIntensity, Collectors.counting()));

        // Workouts logged over past 7 days
        Map<String, Integer> dailyLogs = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            String dayKey = day.getDayOfWeek().name().substring(0, 3) + " " + day.getDayOfMonth();
            int dayCount = (int) workouts.stream().filter(w -> w.getWorkoutDate().equals(day)).count();
            dailyLogs.put(dayKey, dayCount);
        }

        data.put("workoutsByType", workoutsByType);
        data.put("workoutsByIntensity", workoutsByIntensity);
        data.put("dailyWorkoutTrends", dailyLogs);
        return data;
    }

    public static int estimateCalories(String type, int duration, String intensity, Double weightKg) {
        double weight = (weightKg != null && weightKg > 20) ? weightKg : 70.0;
        double met;
        switch (type.toLowerCase()) {
            case "running":
                met = 9.8;
                break;
            case "cycling":
                met = 7.5;
                break;
            case "swimming":
                met = 8.0;
                break;
            case "hiit":
                met = 9.0;
                break;
            case "gym & weight training":
            case "gym":
            case "strength training":
                met = 6.0;
                break;
            case "yoga & flexibility":
            case "yoga":
                met = 3.5;
                break;
            case "walking":
                met = 3.8;
                break;
            default:
                met = 5.0;
        }

        double intensityMultiplier = 1.0;
        if ("low".equalsIgnoreCase(intensity)) {
            intensityMultiplier = 0.85;
        } else if ("high".equalsIgnoreCase(intensity)) {
            intensityMultiplier = 1.25;
        }

        // Calories = MET * Weight(kg) * Duration(hours) * Intensity
        double durationHours = duration / 60.0;
        double burned = met * weight * durationHours * intensityMultiplier;
        return (int) Math.round(burned);
    }
}
