package managers;

import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    List<Task> getEpicsSubs(Epic epic);

    void deleteTasks();

    void deleteEpics();

    void removeSubs();

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    Epic getEpicByID(int id);

    Task getTaskByID(int id);

    Subtask getSubtaskByID(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void deleteEpicByID(int id);

    void deleteTaskByID(int id);

    void deleteSubTaskByID(int id);

    void refreshEpic(Epic epic);

    void refreshTusk(Task task);

    void refreshSubtask(Subtask subtask);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
