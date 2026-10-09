# FitTrack Pro — Online Fitness Tracking Application

FitTrack Pro is a web-based fitness tracking application developed using Java and Spring Boot. It provides a platform for recording workouts, tracking fitness progress, managing challenges, and administering fitness-related content.

This project was developed as a college project to demonstrate full-stack web development, database integration, and cloud deployment.

## 🚀 Live Application

**Live Website:** https://fitness-tracker-07me.onrender.com

**GitHub Repository:** https://github.com/Prince-pandey2711/fitness-tracker

The application is deployed on Render using Docker.

> **Note:** The application runs on Render's free hosting plan. The service may take some time to start after inactivity, and local database files on the free instance are not guaranteed to persist across restarts or redeployments.

---

## ✨ Features

### User Features

- **Workout Tracking:** Record fitness activities, workout duration, intensity, calories, notes, and dates.
- **Workout Management:** View, edit, and delete workout records, if enabled in the application.
- **Progress Tracking:** Review workout history and fitness progress.
- **Fitness Challenges:** Participate in available fitness challenges and track progress.
- **Profile Management:** Manage personal and fitness-related information, if supported.
- **Community Content:** Submit fitness-related content for review, if supported.

### Administrator Features

- **User Management:** Manage user accounts and roles, if implemented.
- **Content Moderation:** Review submitted fitness content, if implemented.
- **System Configuration:** Manage application settings, if implemented.
- **Fitness Statistics:** View fitness activity statistics, if implemented.
- **Activity Monitoring:** Review system activity events, if implemented.

*Note: Feature availability depends on the functionality implemented in the current deployed version.*

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language and runtime |
| Spring Boot | Backend application framework |
| Spring MVC | Web request handling |
| Spring Data JPA | Database access and persistence |
| Hibernate | Object-relational mapping |
| Thymeleaf | Server-side HTML templates |
| HTML and CSS | Web interface |
| H2 Database | Database for storing application data |
| Maven | Dependency management and build |
| Docker | Application packaging and deployment |
| Git and GitHub | Version control and source code hosting |
| Render | Cloud application hosting |

---

## 🗄️ Database

The application is configured to use an H2 file-based database.

**Database file location in the project:**

`data/fitnessdb`

The database schema is managed by Hibernate according to the application's configuration.

**Important:** On Render's free web service, the local filesystem is ephemeral. Do not rely on the deployed H2 database file as permanent storage. Keep backups of important data.

The H2 database console is intended for development and should remain disabled in production unless there is a specific, secured reason to enable it.

---

## 💻 Run the Project Locally

### Prerequisites

- Java 17 or a compatible JDK
- Git
- Maven Wrapper included in the repository

### 1. Clone the repository

```bash
git clone https://github.com/Prince-pandey2711/fitness-tracker.git
```

### 2. Open the project directory

```bash
cd fitness-tracker
```

### 3. Start the application on Windows

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, on macOS or Linux:

```bash
./mvnw spring-boot:run
```

### 4. Open the application

Visit:

http://localhost:8080

The application should load once Spring Boot has finished starting.

---

## ☁️ Deployment

The application is deployed to Render using a Dockerfile.

Deployment workflow:

1. Develop the application locally.
2. Commit and push changes to GitHub.
3. Render builds the application using Docker.
4. Render deploys the application.
5. Access the live application through the public Render URL.

**Live URL:** https://fitness-tracker-07me.onrender.com

---

## 🎯 Project Objectives

- Develop a web application using Java and Spring Boot.
- Implement web-based fitness activity tracking.
- Integrate a database using Spring Data JPA and Hibernate.
- Create a user interface with Thymeleaf, HTML, and CSS.
- Practice version control using Git and GitHub.
- Deploy a Java web application to a cloud hosting platform using Docker.

---

## 🔮 Future Enhancements

- Add persistent cloud database storage.
- Improve authentication and password security.
- Add more detailed fitness reports and visualizations.
- Improve mobile responsiveness.
- Add automated testing and continuous integration.

---

## 👨‍🎓 Project Information

**Project Name:** FitTrack Pro — Online Fitness Tracking Application

**Project Type:** College Project

**Backend:** Java and Spring Boot

**Database:** H2

**Deployment Platform:** Render

**Source Code:** https://github.com/Prince-pandey2711/fitness-tracker

**Live Demo:** https://fitness-tracker-07me.onrender.com

---

*Developed as an educational project to demonstrate Java-based web application development and deployment.*