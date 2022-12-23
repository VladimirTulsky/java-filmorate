package managers;

import exceptions.ManagerSaveException;
import taskss.Epic;
import taskss.Status;
import taskss.Subtask;
import taskss.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryManager {

    private final String file;

    public FileBackedTasksManager(String file) {
        this.file = file;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = super.getSubtaskByID(id);
        save();
        return subtask;
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteSubTaskByID(int id) {
        super.deleteSubTaskByID(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void removeSubs() {
        super.removeSubs();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void refreshEpic(Epic epic) {
        super.refreshEpic(epic);
        save();
    }

    @Override
    public void refreshTusk(Task task) {
        super.refreshTusk(task);
        save();
    }

    @Override
    public void refreshSubtask(Subtask subtask) {
        super.refreshSubtask(subtask);
        save();
    }

    public static FileBackedTasksManager loadFromFile(String file) {
        try {
            FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
            String csv = Files.readString(Path.of(file));
            csv = csv.replaceAll("\r", "");
            List<Integer> history;
            String[] lines = csv.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    line = lines[i + 1];
                    history = historyFromString(line);
                    for (Integer in : history) {
                        if (tasksManager.epics.containsKey(in)) {
                            Epic epic = tasksManager.epics.get(in);
                            tasksManager.defaultHistory.add(epic);
                        } else if (tasksManager.subtasks.containsKey(in)) {
                            Subtask subtask = tasksManager.subtasks.get(in);
                            tasksManager.defaultHistory.add(subtask);
                        } else {
                            Task task = tasksManager.tasks.get(in);
                            tasksManager.defaultHistory.add(task);
                        }
                    }
                    break;
                }
                Task task = tasksManager.taskFromString(line);
                int idd = task.getId();
                if (idd > tasksManager.id) {
                    tasksManager.id = idd;
                }
                tasksManager.add(task);
            }
            for (Subtask sub : tasksManager.subtasks.values()) {
                tasksManager.epics.get(sub.epicId).getSubtasks().add(sub.getId());
            }
            return tasksManager;
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    public void add(Task task) {
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                Epic epic = (Epic) task;
                epics.put(epic.getId(), epic);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(subtask.getId(), subtask);
                break;
        }
    }

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
            writer.write("\n" + historyToString(defaultHistory));
            writer.close();
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    public String toString(Task task) {
        String strTask = task.getId() + "," + task.getType() + "," + task.getTitle() + ","
                + task.getStatus() + "," + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime();
        if (subtasks.containsKey(task.getId())) {
            Subtask subtask = subtasks.get(task.getId());
            strTask += "," + subtask.getEpic();
        }
        return strTask;
    }

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            stringBuilder.append(task.getId()).append(",");
        }
        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(String value) {
        String[] history = value.split(",");
        List<Integer> hist = new ArrayList<>();
        for (String str : history) {
            hist.add(Integer.parseInt(str));
        }
        return hist;
    }

    public Task taskFromString(String value) {
        String[] values = value.split(",");
        switch (values[1].toUpperCase()) {
            case "TASK":
                Task task = new Task(values[2], values[4]);
                task.setStatus(Status.valueOf(values[3]));
                task.setId(Integer.parseInt(values[0]));
                task.setDuration(Integer.parseInt(values[5]));
                task.setStartTime(LocalDateTime.parse(values[6]));
                return task;
            case "EPIC":
                Epic epic = new Epic(values[2], values[4]);
                epic.setStatus(Status.valueOf(values[3]));
                epic.setId(Integer.parseInt(values[0]));
                epic.setDuration(Integer.parseInt(values[5]));
                epic.setStartTime(LocalDateTime.parse(values[6]));
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(values[2], values[4], Integer.parseInt(values[7]));
                subtask.setStatus(Status.valueOf(values[3]));
                subtask.setId(Integer.parseInt(values[0]));
                subtask.setDuration(Integer.parseInt(values[5]));
                subtask.setStartTime(LocalDateTime.parse(values[6]));
                return subtask;
        }
        return null;
    }

    static void main(String[] args) {
        String file = ("C:/Users/Saske/Desktop/test/11.csv");
        FileBackedTasksManager inMemoryManager = FileBackedTasksManager.loadFromFile(file);
        System.out.println(inMemoryManager.getHistory());
        Epic epic3 = new Epic("test", "test");
        Subtask subtask4 = new Subtask("Tesst", "Desc", 4);
        inMemoryManager.createEpic(epic3);
        LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        epic3.setStartTime(localDateTime);
        inMemoryManager.createSubtask(subtask4);
        subtask4.setDuration(20);
        inMemoryManager.getEpicByID(2);
        inMemoryManager.getSubtaskByID(3);
        inMemoryManager.getEpicByID(2);
        System.out.println(inMemoryManager.getHistory());
        inMemoryManager.save();
    }
}
