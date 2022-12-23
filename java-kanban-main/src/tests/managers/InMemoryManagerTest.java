package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskss.Epic;
import taskss.Status;
import taskss.Subtask;
import taskss.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryManagerTest {
    static InMemoryManager inMemoryManager;
    static Epic epic1;
    static Epic epic2;
    static Subtask subtask1;
    static Subtask subtask2;
    static Task task1;
    static Task task2;

    @BeforeEach
    public void createManager() {
        inMemoryManager = new InMemoryManager();
        epic1 = new Epic("Test", "test");
        epic2 = new Epic("Test2", "test");
        subtask1 = new Subtask("Составить список покупок", "Desc", 1);
        subtask2 = new Subtask("Купить все по списку", "Desc", 1);
        task1 = new Task("Test", "test");
        task2 = new Task("Test2", "test");
        task1.setStartTime(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        task2.setStartTime(LocalDateTime.of(2, 1, 1, 1, 1, 1));
        subtask1.setStartTime(LocalDateTime.of(3, 1, 1, 1, 1, 1));
        subtask2.setStartTime(LocalDateTime.of(4, 1, 1, 1, 1, 1));
        epic1.setStartTime(LocalDateTime.of(5, 1, 1, 1, 1, 1));
        epic2.setStartTime(LocalDateTime.of(6, 1, 1, 1, 1, 1));
        task1.setEndTime(LocalDateTime.of(1, 1, 1, 1, 1, 2));
        task2.setEndTime(LocalDateTime.of(2, 1, 1, 1, 1, 2));
        subtask1.setEndTime(LocalDateTime.of(3, 1, 1, 1, 1, 2));
        subtask2.setEndTime(LocalDateTime.of(4, 1, 1, 1, 1, 2));
        epic1.setEndTime(LocalDateTime.of(5, 1, 1, 1, 1, 2));
        epic2.setEndTime(LocalDateTime.of(6, 1, 1, 1, 1, 2));
        inMemoryManager.createEpic(epic1);
        inMemoryManager.createEpic(epic2);
        inMemoryManager.createSubtask(subtask1);
        inMemoryManager.createSubtask(subtask2);
        inMemoryManager.createTask(task1);
        inMemoryManager.createTask(task2);
    }

    @Test
    void shouldReturn2and3subtasks() {
        ArrayList<Task> test = new ArrayList<>();
        test.add(subtask1);
        test.add(subtask2);
        assertEquals(test, inMemoryManager.getEpicsSubs(epic1));
    }

    @Test
    void shouldReturnEmptySubtasks() {
        inMemoryManager.removeSubs();
        ArrayList<Integer> test = new ArrayList<>();
        assertEquals(test, inMemoryManager.getEpicsSubs(epic1));
    }

    @Test
    void shouldReturnEmptyTasks() {
        inMemoryManager.deleteTasks();
        HashMap<Integer, Task> test1 = new HashMap<>();
        assertEquals(test1, inMemoryManager.tasks);
    }

    @Test
    void shouldReturnEmptyEpics() {
        inMemoryManager.deleteEpics();
        HashMap<Integer, Epic> test1 = new HashMap<>();
        assertEquals(test1, inMemoryManager.epics);
    }

    @Test
    void shouldReturnEmptySubtasksInEpic1() {
        inMemoryManager.removeSubs();
        ArrayList<Integer> testSubtasks = new ArrayList<>();
        assertEquals(testSubtasks, epic1.getSubtasks());
    }

    @Test
    void shouldReturnEpic1() {
        assertEquals(epic1, inMemoryManager.getEpicByID(1));
    }

    @Test
    void shouldReturnTask1() {
        assertEquals(task1, inMemoryManager.getTaskByID(5));
    }

    @Test
    void shouldReturnSubtask1() {
        assertEquals(subtask1, inMemoryManager.getSubtaskByID(3));
    }

    @Test
    void shouldReturnNullEpic() {
        inMemoryManager.deleteEpicByID(1);
        assertNull(inMemoryManager.getEpicByID(1));
    }

    @Test
    void shouldReturnNullTask() {
        inMemoryManager.deleteTaskByID(1);
        assertNull(inMemoryManager.getTaskByID(1));
    }

    @Test
    void shouldReturnNullSubtask() {
        inMemoryManager.deleteSubTaskByID(2);
        assertNull(inMemoryManager.getSubtaskByID(2));
    }

    @Test
    void shouldReturnNewDesc() {
        epic1.setDescription("NewTest");
        inMemoryManager.refreshEpic(epic1);
        assertEquals("NewTest", epic1.getDescription());
    }

    @Test
    void shouldReturnInProgressForTask() {
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryManager.refreshTusk(task1);
        assertEquals(Status.IN_PROGRESS, task1.getStatus());
    }

    @Test
    void shouldReturnInProgressForSubtask() {
        subtask1.setStatus(Status.IN_PROGRESS);
        inMemoryManager.refreshSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, subtask1.getStatus());
    }

    @Test
    void shouldReturnActualHistory() {
        inMemoryManager.getEpicByID(1);
        inMemoryManager.getSubtaskByID(3);
        ArrayList<Task> testHistory = new ArrayList<>();
        testHistory.add(epic1);
        testHistory.add(subtask1);
        assertEquals(testHistory, inMemoryManager.getHistory());
    }

}