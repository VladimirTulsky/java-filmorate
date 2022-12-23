package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskss.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    void shouldReturnNotEmptyHistory() {
        Task task = new Task("Test", "test");
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(historyManager, "История пуста");
        Assertions.assertEquals(1, history.size(), "История пуста");
    }

    @Test
    void shouldReturnEmptyHistoryWhenDeletionInTheStart() {
        Task task = new Task("Test", "test");
        Task task2 = new Task("Test2", "test2");
        Task task3 = new Task("Test3", "test3");
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        history.remove(0);
        final List<Task> emptyHistory = new ArrayList<>();
        assertEquals(emptyHistory, history);
    }

    @Test
    void shouldReturnEmptyHistoryWhenDeletionInTheEnd() {
        Task task = new Task("Test", "test");
        Task task2 = new Task("Test2", "test2");
        Task task3 = new Task("Test3", "test3");
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        history.remove(2);
        List<Task> newHistory = new ArrayList<>();
        newHistory.add(task);
        newHistory.add(task2);
        assertEquals(newHistory, history);
    }

    @Test
    void shouldReturnEmptyHistoryWhenDeletionInTheMid() {
        Task task = new Task("Test", "test");
        Task task2 = new Task("Test2", "test2");
        Task task3 = new Task("Test3", "test3");
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        history.remove(1);
        List<Task> newHistory = new ArrayList<>();
        newHistory.add(task);
        newHistory.add(task3);
        assertEquals(newHistory, history);
    }
}