package managers;

import java.net.URI;

public class Managers {

    public static HTTPTaskManager getDefault() {
        return new HTTPTaskManager(URI.create("http://localhost:8078"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFile(){
        String file = ("C:/Users/Saske/Desktop/test/11.csv");
        FileBackedTasksManager fileBackedMemoryManager = FileBackedTasksManager.loadFromFile(file);
        return fileBackedMemoryManager;
    }
}
