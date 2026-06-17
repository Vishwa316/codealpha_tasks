# ☕ Java Application using Gradle

> **Code Alpha Internship Task** — A fully functional DevOps Task Planner CLI application built in Java, managed and automated with Gradle, with JUnit 5 unit tests and a GitHub Actions CI/CD pipeline.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

---

## 📋 Table of Contents
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Gradle Commands Reference](#-gradle-commands-reference)
- [Running Tests](#-running-tests)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Key Concepts](#-key-concepts)

---

## ✨ Features
- 📝 **Task Management** — Add, complete, filter, and sort DevOps tasks
- 🔢 **Priority System** — HIGH / MEDIUM / LOW enum-based priorities
- 📅 **Due Date Tracking** — Overdue detection with `LocalDate` comparison
- 📊 **Summary Report** — Completion rate with ASCII progress bar
- ✅ **12 Unit Tests** — Full JUnit 5 coverage via `@Test`, `@BeforeEach`, `@DisplayName`
- 🤖 **Automated CI/CD** — GitHub Actions builds & tests on every push across Java 17 & 21
- 🔧 **Custom Gradle Task** — `./gradlew projectInfo` prints build environment info

---

## 📁 Project Structure

```
java-gradle-app/
│
├── 📄 settings.gradle              # Root project settings & module registration
├── 📄 gradle.properties            # Gradle performance tuning flags
├── 📄 gradlew / gradlew.bat        # Gradle Wrapper scripts (no local install needed)
│
├── 📂 gradle/wrapper/              # Pinned Gradle version for reproducibility
│   └── gradle-wrapper.properties
│
└── 📂 app/                         # Main application module
    ├── 📄 build.gradle             # Build config, dependencies, and custom tasks
    └── 📂 src/
        ├── 📂 main/java/com/devops/
        │   └── ☕ App.java          # DevOps Task Planner application logic
        └── 📂 test/java/com/devops/
            └── ☕ AppTest.java      # JUnit 5 unit tests (12 tests)
```

---

## 🛠️ Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Java (JDK) | 17 or 21+ | [adoptium.net](https://adoptium.net) |
| Gradle | via Wrapper | No install needed — use `./gradlew` |

> ✅ **No Gradle installation required!** The project includes a **Gradle Wrapper** (`gradlew`). Just use `./gradlew` instead of `gradle` and it downloads the correct version automatically.

Verify Java:
```bash
java -version
```

---

## 🚀 Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/code-alpha-internship.git
cd code-alpha-internship/java-gradle-app

# 2. Build the project (compiles all source files)
./gradlew build

# 3. Run the application
./gradlew run
```

### Expected Output
```
══════════════════════════════════════════════════════════════════════
  DevOps Task Planner  |  Built with Gradle  |  Code Alpha Internship
══════════════════════════════════════════════════════════════════════

  ── All Tasks (sorted by Priority → Due Date) ──────────────────────
  #1   [HIGH  ] ✓ DONE   Setup Docker Environment            Due: Jun 15, 2026
  #2   [HIGH  ] ○ PENDING Write Dockerfile for Web Server    Due: Jun 18, 2026
  #3   [HIGH  ] ✓ DONE   Configure docker-compose.yml        Due: Jun 19, 2026
  ...

══════════════════════════════════════════════════════════════════════
  Project Summary Report
══════════════════════════════════════════════════════════════════════
  Total Tasks     : 8
  Completed       : 3
  Pending         : 5
  Overdue         : 1
  Completion Rate : 37.5%

  Progress: [███████░░░░░░░░░░░░░] 37.5%
```

---

## 🔧 Gradle Commands Reference

### Core Lifecycle Tasks

| Command | Description |
|---------|-------------|
| `./gradlew build` | Compile sources + run tests + package JAR |
| `./gradlew run` | Compile and run the application |
| `./gradlew test` | Run all JUnit 5 unit tests |
| `./gradlew clean` | Delete all build outputs (`build/` directories) |
| `./gradlew clean build` | Full clean rebuild from scratch |
| `./gradlew jar` | Package compiled code into a JAR file |
| `./gradlew distZip` | Create a distributable ZIP archive |

### Inspection & Debugging Tasks

```bash
# List all available Gradle tasks
./gradlew tasks

# Show all project dependencies as a tree
./gradlew dependencies

# Show detailed build scan info
./gradlew build --scan

# Custom task: print project + environment info
./gradlew projectInfo

# Check for outdated dependencies
./gradlew dependencyUpdates
```

### Understanding the Build Lifecycle

```
init → configure → compileJava → processResources → classes
     → compileTestJava → testClasses → test → jar → build
```

---

## ✅ Running Tests

```bash
# Run all tests
./gradlew test

# Run with verbose output
./gradlew test --info

# Run a specific test class
./gradlew test --tests "com.devops.AppTest"

# Run and keep going even if tests fail
./gradlew test --continue
```

### Test Report
After running tests, open the HTML report:
```
app/build/reports/tests/test/index.html
```

### Test Coverage (12 Tests)

| Test | What it verifies |
|------|-----------------|
| `testTaskCreation` | Task is created with correct ID, title, priority, due date |
| `testAutoIncrementIds` | Task IDs increment correctly: 1, 2, 3... |
| `testBlankTitleThrowsException` | Blank title throws `IllegalArgumentException` |
| `testNullTitleThrowsException` | Null title throws `IllegalArgumentException` |
| `testCompleteTask` | Task is marked done and moves to completed list |
| `testCompleteNonExistentTask` | Returns false for unknown task ID |
| `testGetPendingTasks` | Filters only non-completed tasks |
| `testGetOverdueTasks` | Identifies only incomplete past-due tasks |
| `testGetTotalCount` | Returns accurate total task count |
| `testSortByPriority` | HIGH → MEDIUM → LOW ordering is correct |
| `testPriorityLabels` | Each priority returns the correct label string |
| `testCompletedTaskNotOverdue` | A completed task is never flagged as overdue |

---

## 🤖 CI/CD Pipeline

This project uses **GitHub Actions** to automatically run the build and tests on every `push` or `pull_request` to `main`.

### Workflow: `.github/workflows/gradle.yml`

```
Push to main
    │
    ├─► [Java 17] Checkout → Setup JDK → Cache Gradle → Build → Test → Run → Upload Report
    └─► [Java 21] Checkout → Setup JDK → Cache Gradle → Build → Test → Run → Upload Report
```

### Pipeline Steps
1. ✅ **Checkout** — Downloads the latest source code
2. ✅ **Setup JDK** — Installs Eclipse Temurin JDK (17 or 21)
3. ✅ **Cache Gradle** — Caches `~/.gradle` for faster subsequent runs
4. ✅ **Build** — Compiles all Java source files
5. ✅ **Test** — Runs all 12 JUnit 5 tests
6. ✅ **Run** — Executes the application and prints its output
7. ✅ **projectInfo** — Runs the custom Gradle task
8. ✅ **Upload Reports** — Stores test HTML reports as downloadable artifacts

---

## 📚 Key Concepts

### Why Gradle?
| Feature | Benefit |
|---------|---------|
| **Incremental Builds** | Only recompiles changed files → faster builds |
| **Dependency Management** | Downloads JARs from Maven Central automatically |
| **Gradle Wrapper** | Team uses the same Gradle version — no "works on my machine" issues |
| **Build Lifecycle** | Standardized phases: compile → test → package → deploy |
| **Custom Tasks** | Extend builds with project-specific automation |

### Project Dependencies
```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter'  // JUnit 5 testing
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    implementation 'com.google.guava:guava'               // Google utilities
}
```

---

> 🎓 Built as part of **Code Alpha Internship — DevOps Track**, demonstrating Java application automation with Gradle, unit testing, and CI/CD integration using GitHub Actions.
