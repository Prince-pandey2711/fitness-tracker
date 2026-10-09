package com.college.fitnesstracker.config;

import com.college.fitnesstracker.model.*;
import com.college.fitnesstracker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final FitnessContentRepository fitnessContentRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final ActivityLogRepository activityLogRepository;

    public DataInitializer(UserRepository userRepository,
                           WorkoutRepository workoutRepository,
                           ChallengeRepository challengeRepository,
                           UserChallengeRepository userChallengeRepository,
                           FitnessContentRepository fitnessContentRepository,
                           SystemSettingRepository systemSettingRepository,
                           ActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.fitnessContentRepository = fitnessContentRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // 1. Seed Users
            User admin = new User("System Administrator", "admin@fitness.com", "Password123!", "ADMIN");
            admin.setFitnessGoal("General Health");
            userRepository.save(admin);

            User jane = new User("Jane Doe", "user@fitness.com", "Password123!", "USER");
            jane.setFitnessGoal("Weight Loss");
            jane.setWeight(65.0);
            jane.setHeight(168.0);
            jane.setTargetMinutesPerWeek(180);
            userRepository.save(jane);

            User alex = new User("Alex Rivera", "alex@fitness.com", "Password123!", "USER");
            alex.setFitnessGoal("Muscle Gain");
            alex.setWeight(78.0);
            alex.setHeight(182.0);
            alex.setTargetMinutesPerWeek(210);
            userRepository.save(alex);

            // 2. Seed Workouts
            workoutRepository.save(new Workout(jane, "Running", 35, "HIGH", 380, "Morning interval run around park", LocalDate.now().minusDays(1)));
            workoutRepository.save(new Workout(jane, "Yoga & Flexibility", 40, "LOW", 140, "Vinyasa flow and hip openers", LocalDate.now().minusDays(2)));
            workoutRepository.save(new Workout(jane, "HIIT", 30, "HIGH", 320, "Tabata bodyweight circuits", LocalDate.now().minusDays(3)));
            workoutRepository.save(new Workout(jane, "Cycling", 45, "MEDIUM", 350, "Scenic outdoor ride", LocalDate.now().minusDays(5)));

            workoutRepository.save(new Workout(alex, "Gym & Weight Training", 60, "HIGH", 420, "Heavy chest & triceps push session", LocalDate.now()));
            workoutRepository.save(new Workout(alex, "Gym & Weight Training", 50, "HIGH", 370, "Back and biceps pull day", LocalDate.now().minusDays(2)));
            workoutRepository.save(new Workout(alex, "Running", 25, "MEDIUM", 230, "Incline treadmill cooldown run", LocalDate.now().minusDays(3)));

            // 3. Seed Challenges
            Challenge cardioChallenge = new Challenge(
                    "30-Day Cardio Blast",
                    "Accumulate at least 300 minutes of heart-pumping cardio activities (Running, Cycling, HIIT) this month.",
                    "Cardio",
                    300,
                    12,
                    LocalDate.now().minusDays(5),
                    LocalDate.now().plusDays(25),
                    "ACTIVE",
                    "Cardio Champion"
            );
            challengeRepository.save(cardioChallenge);

            Challenge strengthChallenge = new Challenge(
                    "Iron Lifter Strength Series",
                    "Log 10 gym or strength training sessions to build pure power and muscle mass.",
                    "Strength",
                    400,
                    10,
                    LocalDate.now().minusDays(3),
                    LocalDate.now().plusDays(27),
                    "ACTIVE",
                    "Titan Lifter"
            );
            challengeRepository.save(strengthChallenge);

            Challenge mobilityChallenge = new Challenge(
                    "Autumn Mobility & Zen Reset",
                    "Complete 8 sessions of Yoga and flexibility training to enhance functional recovery.",
                    "Flexibility",
                    240,
                    8,
                    LocalDate.now().minusDays(30),
                    LocalDate.now().minusDays(2),
                    "COMPLETED",
                    "Zen Master"
            );
            challengeRepository.save(mobilityChallenge);

            // 4. Seed User Challenges (Participations & History)
            UserChallenge janeCardio = new UserChallenge(jane, cardioChallenge);
            janeCardio.setStatus("IN_PROGRESS");
            janeCardio.setCurrentProgress(65);
            janeCardio.setResult("In Progress (65% completed)");
            userChallengeRepository.save(janeCardio);

            UserChallenge janeMobility = new UserChallenge(jane, mobilityChallenge);
            janeMobility.setStatus("COMPLETED");
            janeMobility.setCurrentProgress(100);
            janeMobility.setCompletedAt(LocalDateTime.now().minusDays(3));
            janeMobility.setResult("Completed Successfully! Earned Badge: Zen Master");
            userChallengeRepository.save(janeMobility);

            UserChallenge alexStrength = new UserChallenge(alex, strengthChallenge);
            alexStrength.setStatus("IN_PROGRESS");
            alexStrength.setCurrentProgress(40);
            alexStrength.setResult("In Progress (40% completed)");
            userChallengeRepository.save(alexStrength);

            // 5. Seed Fitness Content
            FitnessContent approvedArticle1 = new FitnessContent(
                    admin,
                    "The Ultimate Guide to Pre & Post-Workout Nutrition",
                    "Nutrition & Diet",
                    "Fueling your body before exercise primes muscle glycogen reserves, while post-workout protein (20-30g) triggers muscle protein synthesis. Combine whey or plant protein with fast-digesting carbohydrates within 45 minutes of completing intense exercise for optimal glycogen replenishment."
            );
            approvedArticle1.setStatus("APPROVED");
            approvedArticle1.setAdminNotes("Verified by certified fitness nutrition advisor.");
            approvedArticle1.setReviewedAt(LocalDateTime.now().minusDays(2));
            fitnessContentRepository.save(approvedArticle1);

            FitnessContent approvedArticle2 = new FitnessContent(
                    jane,
                    "5 Daily Mobility Drills to Eradicate Lower Back Stiffness",
                    "Recovery & Mobility",
                    "If you sit at a desk for extended periods, tight hip flexors and inactive glutes can place unnecessary shear strain on your lumbar spine. Practice Cat-Cow, Couch Stretch, 90/90 Hip Swivels, Bird-Dogs, and Glute Bridges daily for 8 minutes."
            );
            approvedArticle2.setStatus("APPROVED");
            approvedArticle2.setAdminNotes("Excellent recovery routine. Approved for community view.");
            approvedArticle2.setReviewedAt(LocalDateTime.now().minusDays(1));
            fitnessContentRepository.save(approvedArticle2);

            FitnessContent pendingArticle = new FitnessContent(
                    alex,
                    "High-Intensity Interval Training (HIIT) vs Steady State Cardio",
                    "Workout Routine",
                    "HIIT generates superior post-exercise oxygen consumption (EPOC), leading to continued caloric expenditure hours after the workout ends. Steady-state Zone 2 training, conversely, is easier on joint recovery and builds baseline aerobic endurance."
            );
            pendingArticle.setStatus("PENDING");
            fitnessContentRepository.save(pendingArticle);

            // 6. Seed System Settings
            systemSettingRepository.save(new SystemSetting("app_name", "FitTrack Pro", "Application branding title", "General"));
            systemSettingRepository.save(new SystemSetting("allow_registrations", "true", "Permit new user account sign-ups", "Registration"));
            systemSettingRepository.save(new SystemSetting("maintenance_mode", "false", "Temporarily lock application for maintenance", "General"));
            systemSettingRepository.save(new SystemSetting("default_weekly_target_mins", "150", "Default weekly workout target in minutes", "Fitness"));
            systemSettingRepository.save(new SystemSetting("max_daily_workout_logs", "5", "Maximum workout entries allowed per user per day", "Fitness"));
            systemSettingRepository.save(new SystemSetting("system_announcement", "Welcome to FitTrack! Check out the active 30-Day Cardio Blast challenge in your dashboard.", "Site-wide announcement banner", "Notifications"));

            // 7. Seed Activity Logs
            activityLogRepository.save(new ActivityLog("admin@fitness.com", "SYSTEM_INITIALIZED", "System database setup and initial entities initialized", "SYSTEM"));
            activityLogRepository.save(new ActivityLog("admin@fitness.com", "USER_MANAGEMENT", "Admin accounts and initial user groups verified", "ADMIN"));
            activityLogRepository.save(new ActivityLog("user@fitness.com", "WORKOUT_LOGGED", "Jane Doe logged 35 min Running session", "USER"));
            activityLogRepository.save(new ActivityLog("user@fitness.com", "CHALLENGE_JOINED", "Jane Doe joined 30-Day Cardio Blast", "USER"));
            activityLogRepository.save(new ActivityLog("alex@fitness.com", "CONTENT_SUBMITTED", "Alex Rivera submitted article: HIIT vs Steady State Cardio", "USER"));
        }
    }
}
