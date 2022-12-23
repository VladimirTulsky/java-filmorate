package managers;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.InstantAdapter;
import server.KVServer;
import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;


class HTTPTaskManagerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private HTTPTaskManager httpTaskManager = new HTTPTaskManager(URI.create("http://localhost:8080"));

    @BeforeEach
    void HttpTaskServerTest() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.startServer();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stopServer();
        kvServer.stop();
    }

    @Test
    void shouldCreateAndLoad() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test", "test");
        Subtask subtask1 = new Subtask("Составить список покупок", "Desc", 1);
        Task task1 = new Task("Test", "test");
        task1.setStartTime(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        subtask1.setStartTime(LocalDateTime.of(3, 1, 1, 1, 1, 1));
        epic1.setStartTime(LocalDateTime.of(5, 1, 1, 1, 1, 1));
        task1.setEndTime(LocalDateTime.of(1, 1, 1, 1, 1, 2));
        subtask1.setEndTime(LocalDateTime.of(3, 1, 1, 1, 1, 2));
        epic1.setEndTime(LocalDateTime.of(5, 1, 1, 1, 1, 2));
        httpTaskManager.createEpic(epic1);
        httpTaskManager.createSubtask(subtask1);
        httpTaskManager.createTask(task1);
        String previousEpic1 = httpTaskManager.getEpicByID(1).toString();
        epic1.setDescription("newTest");
        HTTPTaskManager newHttpTaskManager = httpTaskManager.load();
        httpTaskManager.save();
        Assertions.assertEquals(newHttpTaskManager.getTasks(), httpTaskManager.getTasks());
        Assertions.assertEquals(1, httpTaskManager.getEpics().size());
        Assertions.assertEquals(1, httpTaskManager.getSubtasks().size());
        Assertions.assertNotEquals(previousEpic1, httpTaskManager.getEpicByID(1).toString());
    }

    @Test
    void shouldSaveAndRestoreWithEpicEmptySubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test", "test");
        httpTaskManager.createEpic(epic1);
        httpTaskManager.save();
        HTTPTaskManager httpTaskManager1 = httpTaskManager.load();
        Assertions.assertEquals(httpTaskManager.getEpicByID(1).toString(),httpTaskManager1.getEpicByID(1).toString());
    }
}