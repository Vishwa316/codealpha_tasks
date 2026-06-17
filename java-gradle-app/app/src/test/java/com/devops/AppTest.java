package com.devops;

/*
 * ============================================================
 * AppTest.java — Unit Tests for DevOps Task Planner
 * Code Alpha Internship: Java Application using Gradle
 * ============================================================
 *
 * Tests the core logic of the TaskManager and Task model using
 * JUnit 5 (Jupiter). These tests are run automatically by
 * Gradle via the 'test' task and by GitHub Actions on every push.
 *
 * JUnit 5 Annotations Used:
 *   @Test           — marks a method as a unit test
 *   @BeforeEach     — runs before every individual test
 *   @DisplayName    — provides a human-readable test name
 * ============================================================
 */

import com.devops.App.Priority;
import com.devops.App.Task;
import com.devops.App.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DevOps Task Planner — Unit Test Suite")
class AppTest {

    // A fresh TaskManager is created before each test for isolation
    private TaskManager manager;
    private final LocalDate futureDate  = LocalDate.now().plusDays(5);
    private final LocalDate pastDate    = LocalDate.now().minusDays(3);

    @BeforeEach
    void setUp() {
        // Reset the task manager so each test starts with a clean slate
        manager = new TaskManager();
    }

    // ---- Task Creation Tests ----

    @Test
    @DisplayName("Should create a task with correct properties")
    void testTaskCreation() {
        Task task = manager.addTask("Deploy Nginx", "Run Docker container", Priority.HIGH, futureDate);

        assertEquals(1,             task.getId(),          "First task ID should be 1");
        assertEquals("Deploy Nginx",task.getTitle(),       "Task title should match");
        assertEquals(Priority.HIGH, task.getPriority(),    "Priority should be HIGH");
        assertEquals(futureDate,    task.getDueDate(),     "Due date should match");
        assertFalse(task.isCompleted(),                    "New task should not be completed");
    }

    @Test
    @DisplayName("Should auto-increment task IDs")
    void testAutoIncrementIds() {
        Task task1 = manager.addTask("Task One", "desc", Priority.LOW, futureDate);
        Task task2 = manager.addTask("Task Two", "desc", Priority.LOW, futureDate);
        Task task3 = manager.addTask("Task Three", "desc", Priority.LOW, futureDate);

        assertEquals(1, task1.getId(), "First task ID should be 1");
        assertEquals(2, task2.getId(), "Second task ID should be 2");
        assertEquals(3, task3.getId(), "Third task ID should be 3");
    }

    @Test
    @DisplayName("Should throw exception when task title is blank")
    void testBlankTitleThrowsException() {
        // assertThrows verifies that the expected exception is raised
        assertThrows(IllegalArgumentException.class, () ->
            new Task(1, "  ", "Some desc", Priority.LOW, futureDate),
            "Blank title should throw IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw exception when task title is null")
    void testNullTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task(1, null, "desc", Priority.MEDIUM, futureDate),
            "Null title should throw IllegalArgumentException"
        );
    }

    // ---- Task Completion Tests ----

    @Test
    @DisplayName("Should mark a task as completed by ID")
    void testCompleteTask() {
        manager.addTask("Write Dockerfile", "Create production Nginx Dockerfile", Priority.HIGH, futureDate);

        boolean result = manager.completeTask(1);

        assertTrue(result,                                   "completeTask should return true for valid ID");
        assertTrue(manager.getCompletedTasks().size() == 1, "Completed list should have 1 task");
        assertTrue(manager.getPendingTasks().isEmpty(),      "Pending list should be empty after completion");
    }

    @Test
    @DisplayName("Should return false when completing a non-existent task ID")
    void testCompleteNonExistentTask() {
        manager.addTask("Test Task", "desc", Priority.LOW, futureDate);

        boolean result = manager.completeTask(999); // ID 999 does not exist

        assertFalse(result, "Completing a non-existent task ID should return false");
    }

    // ---- Task Filtering Tests ----

    @Test
    @DisplayName("Should correctly filter pending tasks")
    void testGetPendingTasks() {
        manager.addTask("Task A", "desc", Priority.HIGH,   futureDate);
        manager.addTask("Task B", "desc", Priority.MEDIUM, futureDate);
        manager.addTask("Task C", "desc", Priority.LOW,    futureDate);
        manager.completeTask(1); // Complete Task A

        assertEquals(2, manager.getPendingTasks().size(),
            "Pending tasks should be 2 after completing one of three");
    }

    @Test
    @DisplayName("Should correctly identify overdue tasks")
    void testGetOverdueTasks() {
        manager.addTask("Overdue Task 1",  "desc", Priority.HIGH, pastDate);
        manager.addTask("Overdue Task 2",  "desc", Priority.LOW,  pastDate);
        manager.addTask("On-time Task",    "desc", Priority.HIGH, futureDate);
        manager.completeTask(1); // Complete an overdue task — should NOT appear in overdue

        // Only Task 2 should be overdue (Task 1 is completed, Task 3 is in the future)
        assertEquals(1, manager.getOverdueTasks().size(),
            "Should only count incomplete tasks with past due dates as overdue");
    }

    @Test
    @DisplayName("Should return all tasks count correctly")
    void testGetTotalCount() {
        assertEquals(0, manager.getTotalCount(), "Empty manager should have 0 tasks");

        manager.addTask("Task 1", "desc", Priority.HIGH,   futureDate);
        manager.addTask("Task 2", "desc", Priority.MEDIUM, futureDate);

        assertEquals(2, manager.getTotalCount(), "Manager should report 2 tasks after adding two");
    }

    // ---- Sorting Tests ----

    @Test
    @DisplayName("Should sort tasks by priority: HIGH before MEDIUM before LOW")
    void testSortByPriority() {
        manager.addTask("Low Priority Task",    "desc", Priority.LOW,    futureDate);
        manager.addTask("High Priority Task",   "desc", Priority.HIGH,   futureDate);
        manager.addTask("Medium Priority Task", "desc", Priority.MEDIUM, futureDate);

        var sorted = manager.getAllTasksSorted();

        assertEquals(Priority.HIGH,   sorted.get(0).getPriority(), "First sorted task should be HIGH priority");
        assertEquals(Priority.MEDIUM, sorted.get(1).getPriority(), "Second sorted task should be MEDIUM priority");
        assertEquals(Priority.LOW,    sorted.get(2).getPriority(), "Third sorted task should be LOW priority");
    }

    // ---- Priority Enum Tests ----

    @Test
    @DisplayName("Should return correct label for each priority level")
    void testPriorityLabels() {
        assertEquals("[HIGH  ]", Priority.HIGH.getLabel(),   "HIGH label should match");
        assertEquals("[MEDIUM]", Priority.MEDIUM.getLabel(), "MEDIUM label should match");
        assertEquals("[LOW   ]", Priority.LOW.getLabel(),    "LOW label should match");
    }

    // ---- Overdue Status Tests ----

    @Test
    @DisplayName("Completed tasks should NOT be marked as overdue")
    void testCompletedTaskNotOverdue() {
        manager.addTask("Old Completed Task", "desc", Priority.HIGH, pastDate);
        manager.completeTask(1);

        Task task = manager.getCompletedTasks().get(0);
        assertFalse(task.isOverdue(), "A completed task with a past due date should NOT be overdue");
    }
}
