package taskss;

import managers.InMemoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    static InMemoryManager inMemoryManager;
    static Epic epic1;
    static Epic epic2;
    static Subtask subtask1;
    static Subtask subtask2;

    @BeforeEach
    public void createManager() {
        inMemoryManager = new InMemoryManager();
        epic1 = new Epic("Test", "test");
        epic2 = new Epic("Test2", "test");
        subtask1 = new Subtask("Составить список покупок", "Desc", 1);
        subtask2 = new Subtask("Купить все по списку", "Desc", 1);
        subtask1.setStartTime(LocalDateTime.of(3, 1, 1, 1, 1, 1));
        subtask2.setStartTime(LocalDateTime.of(4, 1, 1, 1, 1, 1));
        epic1.setStartTime(LocalDateTime.of(5, 1, 1, 1, 1, 1));
        epic2.setStartTime(LocalDateTime.of(6, 1, 1, 1, 1, 1));
        subtask1.setEndTime(LocalDateTime.of(3, 1, 1, 1, 1, 2));
        subtask2.setEndTime(LocalDateTime.of(4, 1, 1, 1, 1, 2));
        epic1.setEndTime(LocalDateTime.of(5, 1, 1, 1, 1, 2));
        epic2.setEndTime(LocalDateTime.of(6, 1, 1, 1, 1, 2));
        subtask1.setDuration(1);
        subtask2.setDuration(2);
        inMemoryManager.createEpic(epic1);
        inMemoryManager.createEpic(epic2);
        inMemoryManager.createSubtask(subtask1);
        inMemoryManager.createSubtask(subtask2);
    }

    @Test
    void shouldGiveStatusNewWithoutSubtasks() {
        inMemoryManager.createEpic(epic1);
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void shouldGiveStatusNewWithNewSubtasks() {
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        inMemoryManager.refreshEpic(epic1);
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void shouldGiveStatusDoneWithDoneSubtasks() {
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
        inMemoryManager.createSubtask(subtask1);
        inMemoryManager.createSubtask(subtask2);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        inMemoryManager.refreshEpic(epic1);
        Assertions.assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    void shouldGiveStatusInProgressWithDifferentSubtasks() {
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        inMemoryManager.refreshEpic(epic1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void shouldGiveStatusInProgressWithInProgressSubtasks() {
        inMemoryManager.createEpic(epic1);
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        inMemoryManager.refreshEpic(epic1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void shouldReturnDurationOfEpic3Minutes() {
        assertEquals(3, epic1.getDuration());
        subtask1.setDuration(3);
        inMemoryManager.refreshSubtask(subtask1);
        assertEquals(5, epic1.getDuration());
    }

    @Test
    void shouldReturnStartTimeOfEpic() {
        assertEquals(LocalDateTime.of(3, 1, 1, 1, 1, 1), epic1.getStartTime());
        subtask1.setStartTime(LocalDateTime.of(2, 1, 1, 1, 1, 2));
        inMemoryManager.refreshSubtask(subtask1);
        assertEquals(LocalDateTime.of(2, 1, 1, 1, 1, 2), epic1.getStartTime());
    }

    @Test
    void shouldReturnEndTimeOfEpic() {
        assertEquals(LocalDateTime.of(4, 1, 1, 1, 1, 2), epic1.getEndTime());
        subtask2.setEndTime(LocalDateTime.of(5, 1, 1, 1, 1, 2));
        inMemoryManager.refreshSubtask(subtask2);
        assertEquals(LocalDateTime.of(5, 1, 1, 1, 1, 2), epic1.getEndTime());
    }
}