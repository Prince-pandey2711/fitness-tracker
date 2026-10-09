# FitTrack Pro - Online Fitness Tracking Application

An enterprise-grade, full-stack **Java Spring Boot** application built to fulfill all specifications of the **Online Fitness Tracking Application** (GUVI Geek Network project requirement).

---

## 🚀 Live Application Status
The application is currently compiled, packaged, and running locally on:
- **Application URL:** [http://localhost:8080](http://localhost:8080)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:file:./data/fitnessdb`, User: `sa`, Password: *(blank)*)

---

## 🔑 Default Login Credentials

| User Type | Email Address | Password | Role Description |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@fitness.com` | `Password123!` | Manages users, reviews content, system settings, analytics & real-time monitoring |
| **Standard User** | `user@fitness.com` | `Password123!` | Jane Doe: workout logging, progress analytics, challenges, profile management |
| **Standard User** | `alex@fitness.com` | `Password123!` | Alex Rivera: strength training logs, challenge participant |

*Note: The login page includes convenient **1-click demo presets** to populate credentials instantly.*

---

## 📋 Requirement Fulfillment & Feature Matrix

### 1. User Functionalities & User Dashboard (`/user/dashboard`)

| Feature | Input | Output / Action | Status |
| :--- | :--- | :--- | :--- |
| **1. Workout Logging** | Type (`Running`, `Cycling`, `Gym`, `Yoga`, etc.), Duration (mins), Intensity (`LOW`, `MEDIUM`, `HIGH`), Calories, Notes, Date | Confirmation message for successful workout logging (`Workout logged successfully!`) | ✅ Implemented |
| **1b. Workout Management** | Modal to edit or delete any logged workout | Instant update with confirmation message & real-time recalibration | ✅ Implemented |
| **2. Progress Tracking** | User workout and activity data | Interactive Chart.js visualizations (7-Day Duration Trend, Workout Type Distribution, Intensity Spread, Goal Completion %) | ✅ Implemented |
| **2b. Personalized Guidance** | Weight, Height, Fitness Goal, Logged Sessions | Smart AI Health Engine calculating BMI, target pacing, custom workout and nutritional guidance | ✅ Implemented |
| **3. Fitness Challenges** | Challenge selection (`Join Challenge`) | Challenge participation confirmation, active progress slider modal, and status updates | ✅ Implemented |
| **4. Profile Management** | Name, Email, Password, Weight, Height, Fitness Goal, Weekly Target Minutes | Confirmation message for successful profile update | ✅ Implemented |
| **5. Challenge History** | Past challenge participations | Comprehensive history table showing past challenges, completion status, and badges earned | ✅ Implemented |
| **6. Community Content Contribution** | Title, Category, Body | Submitted content tracking table with approval status (`PENDING`, `APPROVED`, `REJECTED`) and admin notes | ✅ Implemented |

---

### 2. Administrator Functionalities & Admin Dashboard (`/admin/dashboard`)

| Feature | Input | Output / Action | Status |
| :--- | :--- | :--- | :--- |
| **1. User Management** | Name, Email, Password, Role (`ADMIN` / `USER`) | Confirmation message for successful user creation, update, and deletion; full user table with Edit/Delete modals | ✅ Implemented |
| **2. Fitness Content Management** | User-submitted fitness guides / articles | Approve or Reject user-submitted fitness content with custom reviewer notes and status tags | ✅ Implemented |
| **3. System Settings** | App Name, Allow Registrations, Maintenance Mode, Target Minutes, Announcement | Confirmation message for successful settings update; dynamic configuration panel | ✅ Implemented |
| **4. Fitness Statistics** | System-wide workout logs and participation metrics | Graphs and tables showing user engagement trends, workout distribution, intensity breakdown, and challenge management | ✅ Implemented |
| **5. System Activity Monitoring** | Live audit events | Real-time auto-refreshing feed monitoring logins, workout entries, challenge joins, content reviews, and administrative changes | ✅ Implemented |

---

## 🛠️ Technology Stack

- **Backend Framework:** Spring Boot 4.x / Spring Framework 7
- **Language & Runtime:** Java 25 / Java 17 LTS compatible
- **Persistence & ORM:** Spring Data JPA / Hibernate
- **Database:** Persistent file-based H2 Database (`./data/fitnessdb`) with optional MySQL support via `application.properties`
- **Frontend & Templating:** Thymeleaf 3.1+, HTML5, Modern CSS / Tailwind CSS (via CDN)
- **Data Visualizations:** Chart.js (Interactive Bar Charts, Doughnut Charts, Pie Charts)
- **Security & Session Management:** Session-based Role Interceptor (`SessionAuthInterceptor`) ensuring role isolation (`ADMIN` vs `USER`)

---

## 🏃 How to Run the Application

If starting or restarting the server in terminal:

```powershell
# Navigate to project directory
cd "c:\Users\Prince Pandey\OneDrive\Documents\java project"

# Run with Maven Wrapper
.\mvnw.cmd spring-boot:run
```

Once started, open your web browser and navigate to:
**`http://localhost:8080`**
