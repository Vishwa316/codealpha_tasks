package com.devops;

/*
 * ============================================================
 * App.java — DevOps Task Planner
 * Code Alpha Internship: Java Application using Gradle
 * ============================================================
 *
 * A CLI application that manages DevOps tasks with priority
 * levels, due-date tracking, and a completion summary report.
 *
 * Key Java Concepts Demonstrated:
 *   - Object-Oriented Programming (classes, enums, encapsulation)
 *   - Java Collections (ArrayList, stream API, filtering)
 *   - String formatting with printf and format()
 *   - Enum types for type-safe priority levels
 *   - Static factory methods and Builder-style patterns
 *   - Separation of concerns (Task model vs App logic)
 * ============================================================
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ---- Main Application Entry Point ----
public class App {

    // ---- Task Priority Enum ----
    // Using an enum ensures only valid priority values are allowed (type-safe)
    public enum Priority {
        HIGH, MEDIUM, LOW;

        // Returns a colored label for terminal display
        public String getLabel() {
            return switch (this) {
                case HIGH   -> "[HIGH  ]";
                case MEDIUM -> "[MEDIUM]";
                case LOW    -> "[LOW   ]";
            };
        }
    }

    // ---- Task Model (Inner Class) ----
    // Represents a single DevOps task with all its properties
    public static class Task {
        private final int id;
        private final String title;
        private final String description;
        private final Priority priority;
        private final LocalDate dueDate;
        private boolean completed;

        // Constructor: Creates a new Task with all required fields
        public Task(int id, String title, String description, Priority priority, LocalDate dueDate) {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Task title cannot be null or empty.");
            }
            this.id          = id;
            this.title       = title;
            this.description = description;
            this.priority    = priority;
            this.dueDate     = dueDate;
            this.completed   = false;
        }

        // ---- Getters ----
        public int       getId()          { return id; }
        public String    getTitle()       { return title; }
        public String    getDescription() { return description; }
        public Priority  getPriority()    { return priority; }
        public LocalDate getDueDate()     { return dueDate; }
        public boolean   isCompleted()    { return completed; }

        // Marks the task as done
        public void complete() { this.completed = true; }

        // Checks if the task is overdue (past due date and not completed)
        public boolean isOverdue() {
            return !completed && dueDate.isBefore(LocalDate.now());
        }

        // Formatted string representation for terminal display
        @Override
        public String toString() {
            String status   = completed ? "[DONE]   " : (isOverdue() ? "[OVERDUE]" : "[PENDING]");
            String dueFmt   = dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            return String.format("  #%-3d %s %s %-40s Due: %s",
                    id, priority.getLabel(), status, title, dueFmt);
        }
    }

    // ---- Task Manager ----
    // Manages a list of tasks with add, complete, filter, and report functions
    public static class TaskManager {
        private final List<Task> tasks = new ArrayList<>();
        private int nextId = 1;

        // Add a new task to the planner
        public Task addTask(String title, String description, Priority priority, LocalDate dueDate) {
            Task task = new Task(nextId++, title, description, priority, dueDate);
            tasks.add(task);
            return task;
        }

        // Mark a task as completed by its ID. Returns true if found.
        public boolean completeTask(int id) {
            return tasks.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .map(t -> { t.complete(); return true; })
                    .orElse(false);
        }

        // Return all tasks sorted by priority (HIGH first), then by due date
        public List<Task> getAllTasksSorted() {
            return tasks.stream()
                    .sorted(Comparator.comparingInt((Task t) -> t.getPriority().ordinal())
                            .thenComparing(Task::getDueDate))
                    .collect(Collectors.toList());
        }

        // Filter: return only pending (incomplete) tasks
        public List<Task> getPendingTasks() {
            return tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
        }

        // Filter: return only completed tasks
        public List<Task> getCompletedTasks() {
            return tasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
        }

        // Filter: return overdue tasks
        public List<Task> getOverdueTasks() {
            return tasks.stream().filter(Task::isOverdue).collect(Collectors.toList());
        }

        // Get total task count
        public int getTotalCount() { return tasks.size(); }
    }

    // ---- Printer Utilities ----
    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70));
    }

    private static void printSection(String label) {
        System.out.println("\n  -- " + label + " " + "-".repeat(50 - label.length()));
    }

    private static void printTaskList(List<Task> taskList) {
        if (taskList.isEmpty()) {
            System.out.println("  (none)");
        } else {
            taskList.forEach(System.out::println);
        }
    }

    // ---- Main Method ----
    public static void main(String[] args) {

        printHeader("DevOps Task Planner  |  Built with Gradle  |  Code Alpha Internship");

        // ---- Create Task Manager and populate demo tasks ----
        TaskManager manager = new TaskManager();

        // Add sample DevOps tasks with realistic priorities and dates
        manager.addTask("Setup Docker Environment",
                "Install Docker, configure daemon, and verify hello-world container.",
                Priority.HIGH, LocalDate.now().minusDays(2));

        manager.addTask("Write Dockerfile for Web Server",
                "Create multi-stage Dockerfile using Nginx:alpine for production.",
                Priority.HIGH, LocalDate.now().plusDays(1));

        manager.addTask("Configure docker-compose.yml",
                "Define services, health checks, resource limits, and restart policies.",
                Priority.HIGH, LocalDate.now().plusDays(2));

        manager.addTask("Setup GitHub Actions CI Pipeline",
                "Write gradle.yml workflow for automated build and test on push.",
                Priority.MEDIUM, LocalDate.now().plusDays(3));

        manager.addTask("Write Unit Tests with JUnit 5",
                "Test all task manager methods using @Test and @BeforeEach annotations.",
                Priority.MEDIUM, LocalDate.now().plusDays(4));

        manager.addTask("Add Health Check Endpoint in Nginx",
                "Configure /health location block returning 200 OK for Docker checks.",
                Priority.MEDIUM, LocalDate.now().minusDays(1));

        manager.addTask("Document README.md",
                "Write full project documentation with commands, diagrams, and best practices.",
                Priority.LOW, LocalDate.now().plusDays(7));

        manager.addTask("Push Project to GitHub",
                "Initialize Git repo, create .gitignore, commit all files, and push.",
                Priority.LOW, LocalDate.now().plusDays(8));

        // ---- Complete some tasks ----
        manager.completeTask(1); // "Setup Docker Environment" — done!
        manager.completeTask(3); // "Configure docker-compose.yml" — done!
        manager.completeTask(7); // "Document README.md" — done!

        // ---- Display all tasks sorted by priority ----
        printSection("All Tasks (sorted by Priority → Due Date)");
        printTaskList(manager.getAllTasksSorted());

        // ---- Display Pending Tasks ----
        printSection("Pending Tasks (" + manager.getPendingTasks().size() + ")");
        printTaskList(manager.getPendingTasks());

        // ---- Display Overdue Tasks ----
        List<Task> overdue = manager.getOverdueTasks();
        printSection("Overdue Tasks (" + overdue.size() + ")");
        printTaskList(overdue);

        // ---- Print Summary Report ----
        printHeader("Project Summary Report");
        int total     = manager.getTotalCount();
        int completed = manager.getCompletedTasks().size();
        int pending   = manager.getPendingTasks().size();
        double pct    = total > 0 ? (completed * 100.0 / total) : 0;

        System.out.printf("  Total Tasks     : %d%n", total);
        System.out.printf("  Completed       : %d%n", completed);
        System.out.printf("  Pending         : %d%n", pending);
        System.out.printf("  Overdue         : %d%n", overdue.size());
        System.out.printf("  Completion Rate : %.1f%%%n", pct);

        // ASCII progress bar
        int filled = (int) (pct / 5);
        String bar = "[" + "#".repeat(filled) + ".".repeat(20 - filled) + "]";
        System.out.printf("%n  Progress: %s %.1f%%%n", bar, pct);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Build Tool: Gradle | Java Version: " + System.getProperty("java.version", "N/A"));
        System.out.println("=".repeat(70) + "\n");
    }
}
