# 🚀 Code Alpha Internship — DevOps Projects

> Two complete, production-ready DevOps projects built for the **Code Alpha Internship** program, demonstrating containerization, CI/CD automation, and Java application development.

---

## 📦 Projects

### 🐳 [Task 1: Web Server using Docker](./docker-web-server/)
A containerized Nginx web server serving an interactive DevOps dashboard — featuring live container simulation, a Docker CLI cheatsheet, and lifecycle guides.

**Technologies:** Docker, Nginx, HTML/CSS/JavaScript, docker-compose

[![Docker](https://img.shields.io/badge/Docker-29.x-2496ED?style=flat-square&logo=docker&logoColor=white)](./docker-web-server/)
[![Nginx](https://img.shields.io/badge/Nginx-Alpine-009639?style=flat-square&logo=nginx&logoColor=white)](./docker-web-server/)

**Quick Start:**
```bash
cd docker-web-server
docker-compose up --build -d
# Open: http://localhost:8080
```

---

### ☕ [Task 2: Java Application using Gradle](./java-gradle-app/)
A DevOps Task Planner CLI application built in Java with Gradle build automation, 12 JUnit 5 unit tests, and a GitHub Actions CI/CD pipeline.

**Technologies:** Java 21, Gradle 9.5, JUnit 5, GitHub Actions

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](./java-gradle-app/)
[![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?style=flat-square&logo=gradle&logoColor=white)](./java-gradle-app/)
[![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)](./java-gradle-app/)

**Quick Start:**
```bash
cd java-gradle-app
./gradlew run       # Run the application
./gradlew test      # Run all 12 unit tests
```

---

## 🗂️ Repository Structure

```
code-alpha-internship/
│
├── 📄 .gitignore                  # Git ignore rules (Gradle, Java, Docker, IDE)
├── 📄 README.md                   # This file
│
├── 📂 .github/workflows/
│   └── 📄 gradle.yml              # CI/CD: Auto build & test Java app on push
│
├── 📂 docker-web-server/          # Task 1: Docker Web Server
│   ├── 📄 Dockerfile
│   ├── 📄 docker-compose.yml
│   ├── 📄 nginx.conf
│   ├── 📄 README.md
│   └── 📂 src/
│       ├── index.html
│       ├── style.css
│       └── app.js
│
└── 📂 java-gradle-app/            # Task 2: Java App with Gradle
    ├── 📄 settings.gradle
    ├── 📄 gradlew / gradlew.bat
    ├── 📄 README.md
    └── 📂 app/
        ├── 📄 build.gradle
        └── 📂 src/
            ├── main/java/com/devops/App.java
            └── test/java/com/devops/AppTest.java
```

---

## 🎓 About

Built as part of the **Code Alpha Internship — DevOps Track**.

| Task | Topic | Status |
|------|-------|--------|
| Web Server using Docker | Containerization, Nginx, Docker Compose | ✅ Complete |
| Java Application using Gradle | Build Automation, CI/CD, Unit Testing | ✅ Complete |

---

> 💡 Each project folder contains its own detailed `README.md` with full documentation, commands, and explanations.
