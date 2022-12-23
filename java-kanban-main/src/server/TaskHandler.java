package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.FileBackedTasksManager;
import managers.Managers;
import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    ;
    FileBackedTasksManager fileBackedTasksManager = Managers.getDefaultFile();
    String response = "";
    int id;
    private static Gson gson = new Gson();


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        System.out.println("Ќачалась обработка /tasks запроса от клиента.");
        String query = httpExchange.getRequestURI().getQuery();
        switch (method) {
            case "GET":
                if (path.endsWith("/task")) {
                    response = new Gson().toJson(fileBackedTasksManager.getTasks());
                } else if (path.endsWith("/history")) {
                    response = new Gson().toJson(fileBackedTasksManager.getHistory());
                } else if (path.contains("/task/")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    response = new Gson().toJson(fileBackedTasksManager.getTaskByID(id));
                } else if (path.endsWith("/tasks")) {
                    response = new Gson().toJson(fileBackedTasksManager.getPrioritizedTasks());
                } else if (path.contains("/subtask/")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    response = new Gson().toJson(fileBackedTasksManager.getSubtaskByID(id));
                } else if (path.endsWith("/subtask/subtasks")) {
                    response = new Gson().toJson(fileBackedTasksManager.getSubtasks());
                } else if (path.contains("/epic")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    response = new Gson().toJson(fileBackedTasksManager.getEpicByID(id));
                } else if (path.contains("/epic/epics")) {
                    response = new Gson().toJson(fileBackedTasksManager.getEpics());
                }
                break;
            case "POST":
                if (path.endsWith("/task:")) {
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() == 0) {
                        fileBackedTasksManager.createTask(task);
                    } else if (task.getId() <= fileBackedTasksManager.getId()) {
                        fileBackedTasksManager.refreshTusk(task);
                    }
                }
                if (path.endsWith("/subtask")) {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() == 0) {
                        fileBackedTasksManager.createSubtask(subtask);
                    } else if (subtask.getId() <= fileBackedTasksManager.getId()) {
                        fileBackedTasksManager.refreshSubtask(subtask);
                    }
                }
                if (path.endsWith("/epic")) {
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == 0) {
                        fileBackedTasksManager.createEpic(epic);
                    } else if (epic.getId() <= fileBackedTasksManager.getId()) {
                        fileBackedTasksManager.refreshEpic(epic);
                    }
                }
                break;
            case "DELETE":
                if (path.endsWith("/task")) {
                    System.out.println("Ќачалась обработка /tasks/task запроса от клиента.");
                    fileBackedTasksManager.deleteTasks();
                    response = "Tasks Deleted";
                } else if (path.contains("/task/")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    fileBackedTasksManager.deleteTaskByID(id);
                }
                if (path.contains("/subtask")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    fileBackedTasksManager.deleteSubTaskByID(id);
                }
                if (path.endsWith("/epic")) {
                    fileBackedTasksManager.deleteEpics();
                    response = "Epics Deleted";
                } else if (path.contains("/epic")) {
                    id = Integer.parseInt(query.split("=")[1]);
                    fileBackedTasksManager.deleteEpicByID(id);
                }
                break;
        }

        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}

