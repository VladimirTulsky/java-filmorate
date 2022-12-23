import managers.HTTPTaskManager;
import managers.InMemoryManager;
import managers.Managers;
import server.HttpTaskServer;
import server.KVServer;

import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        InMemoryManager manager = new InMemoryManager();
        KVServer kvServer = new KVServer();
        kvServer.start();
        HTTPTaskManager httpTaskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.startServer();
        Task task1 = new Task("Test", "test");
        Task task2 = new Task("Test2", "test");
        task1.setStartTime(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        task2.setStartTime(LocalDateTime.of(2, 1, 1, 1, 1, 1));
        task1.setEndTime(LocalDateTime.of(1, 1, 1, 1, 1, 2));
        task2.setEndTime(LocalDateTime.of(2, 1, 1, 1, 1, 2));
        httpTaskManager.createTask(task1);
        httpTaskManager.createTask(task2);
        System.out.println(httpTaskManager.getTasks().toString());
    }
}
