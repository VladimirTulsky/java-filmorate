package managers;

import com.google.gson.*;
import server.InstantAdapter;
import server.KVTaskClient;
import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final URI uri;
    private final KVTaskClient kvTaskClient;
    Gson gson;
    private HTTPTaskManager httpTaskManager;

    public HTTPTaskManager(URI uri) {
        super(null);
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        this.uri = uri;
        this.kvTaskClient = new KVTaskClient(uri);
    }

    @Override
    public void save(){
        String tasks = gson.toJson(getTasks());
        String subtasks = gson.toJson(getSubtasks());
        String epics = gson.toJson(getEpics());
        String history = gson.toJson(getHistory());
        kvTaskClient.put("tasks", tasks);
        kvTaskClient.put("epics", epics);
        kvTaskClient.put("subtasks", subtasks);
        kvTaskClient.put("history", history);
    }

    public HTTPTaskManager load() throws IOException, InterruptedException {
        HTTPTaskManager httpTaskManager = Managers.getDefault();
        loadTasks(JsonParser.parseString(kvTaskClient.load("tasks")));
        loadEpics(JsonParser.parseString(kvTaskClient.load("epics")));
        loadSubtasks(JsonParser.parseString(kvTaskClient.load("subtasks")));
        loadHistory(JsonParser.parseString(kvTaskClient.load("history")));
        return httpTaskManager;
    }

    public void loadTasks(JsonElement js) {
        if (js.isJsonArray()) {
            JsonArray jsonArray = js.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Task task = gson.fromJson(jsonArray.get(i).toString(), Task.class);
                if (task.getId() >= id) {
                    id = task.getId();
                    httpTaskManager.createTask(task);
                } else {
                    System.out.println("Ошибка");
                }
            }
        }
    }

    public void loadEpics(JsonElement js) {
        if (js.isJsonArray()) {
            JsonArray jsonArray = js.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Epic epic = gson.fromJson(jsonArray.get(i).toString(), Epic.class);
                if (epic.getId() >= id) {
                    id = epic.getId();
                    httpTaskManager.createEpic(epic);
                } else {
                    System.out.println("Ошибка");
                }
            }
        }
    }

    public void loadSubtasks(JsonElement js) {
        if (js.isJsonArray()) {
            JsonArray jsonArray = js.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Subtask subtask = gson.fromJson(jsonArray.get(i).toString(), Subtask.class);
                if (subtask.getId() >= id) {
                    id = subtask.getId();
                    httpTaskManager.createSubtask(subtask);
                } else {
                    System.out.println("Ошибка");
                }
            }
        }
    }

    public void loadHistory(JsonElement js) {
        if (js.isJsonArray()) {
            JsonArray jsonArray = js.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Task task = gson.fromJson(jsonArray.get(i).toString(), Task.class);
                if (task.getId() >= id) {
                    id = task.getId();
                    defaultHistory.add(task);
                } else {
                    System.out.println("Ошибка");
                }
            }
        }
    }

}
