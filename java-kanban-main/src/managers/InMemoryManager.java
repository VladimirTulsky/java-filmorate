package managers;

import exceptions.TimeException;
import taskss.Epic;
import taskss.Subtask;
import taskss.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryManager implements TaskManager {
    protected int id;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager defaultHistory = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public int getId(){
        return id;
    }

    private void addToPrioritizedSet(Task task) throws TimeException {
        prioritizedTasks.add(task);
        crossing(task);
    }

    private void crossing(Task task) throws TimeException {
        List<Task> taskList = getPrioritizedTasks();
        for (int i = 0; i < taskList.size() - 1; i++) {
            if (taskList.get(i).getEndTime().isAfter(taskList.get(i + 1).getStartTime())) {
                prioritizedTasks.removeIf(t -> t.getId() == task.getId());
                throw new TimeException("Задачи пересекаются");
            }
        }
    }

    private int getNextId() {
        return ++id;
    }

    @Override
    public ArrayList<Task> getTasks(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics(){
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getEpicsSubs(Epic epic) {
        List<Task> subtaskss = new ArrayList<>();
        for (Integer sub : epic.getSubtasks()) {
            subtaskss.add(subtasks.get(sub));
        }
        return subtaskss;
    }

    @Override
    public void deleteTasks() {
        for (Integer key : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(key));
            defaultHistory.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Integer epic : epics.keySet()) {
            defaultHistory.remove(epic);
            for (Integer sub : epics.get(epic).getSubtasks()) {
                prioritizedTasks.remove(subtasks.get(sub));
                defaultHistory.remove(sub);
                subtasks.remove(sub);
            }
        }
        epics.clear();
    }

    @Override
    public void removeSubs() {
        for (Integer subtask : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(subtask));
            defaultHistory.remove(subtask);
            epics.get(subtasks.get(subtask).getEpic()).getSubtasks().remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        defaultHistory.add(epic);
        return epic;
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        defaultHistory.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        defaultHistory.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        addToPrioritizedSet(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Integer epicId = subtask.getEpic();
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).getSubtasks().add(subtask.getId());
        refreshEpic(epics.get(subtasks.get(id).getEpic()));
        refreshStatus(epics.get(epicId));
        addToPrioritizedSet(subtask);
    }

    @Override
    public void deleteEpicByID(int id) {
        if (epics.containsKey(id)) {
            for (Integer sub : epics.get(id).getSubtasks()) {
                prioritizedTasks.remove(subtasks.get(sub));
                defaultHistory.remove(sub);
                subtasks.remove(sub);
            }
            epics.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public void deleteSubTaskByID(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpic()).getSubtasks().remove((Integer) id);
            refreshEpic(epics.get(subtasks.get(id).getEpic()));
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            defaultHistory.remove(id);
        }
    }

    @Override
    public void refreshEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        calculateEpicDuration(epic);
        epic.setEndTime(getEpicEndTime(savedEpic));
        epic.setStartTime(getEpicStartTime(savedEpic));
        epic.setDescription(savedEpic.getDescription());
        epic.setTitle(savedEpic.getTitle());
        refreshStatus(epic);
    }

    @Override
    public void refreshTusk(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        addToPrioritizedSet(task);
    }

    @Override
    public void refreshSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpic());
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedSet(subtask);
        refreshEpic(epic);
        refreshStatus(epic);
    }

    private void refreshStatus(Epic epic) {
        if (epic.getSubtasks().stream().allMatch(task -> taskss.Status.NEW.equals(subtasks.get(task).getStatus()))) {
            epic.setStatus(taskss.Status.NEW);
        } else if (epic.getSubtasks().stream().allMatch(task -> taskss.Status.DONE.equals(subtasks.get(task).getStatus()))) {
            epic.setStatus(taskss.Status.DONE);
        } else {
            epic.setStatus(taskss.Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return defaultHistory.getHistory();
    }

    private void calculateEpicDuration(Epic epic) {
        int duration = 0;
        for (int sub : epic.getSubtasks()) {
            duration += subtasks.get(sub).getDuration();
        }
        epic.setDuration(duration);
    }

    private LocalDateTime getEpicEndTime(Epic epic) {
        LocalDateTime endTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        for (int sub : epic.getSubtasks()) {
            LocalDateTime canBeEndTime = subtasks.get(sub).getEndTime();
            if (endTime.isBefore(canBeEndTime)) {
                endTime = canBeEndTime;
            }
        }
        return endTime;
    }

    private LocalDateTime getEpicStartTime(Epic epic) {
        LocalDateTime startTime = LocalDateTime.of(9999, 1, 1, 1, 1, 1);
        for (int sub : epic.getSubtasks()) {
            LocalDateTime canBeStartTime = subtasks.get(sub).getStartTime();
            if (canBeStartTime.isBefore(startTime)) {
                startTime = canBeStartTime;
            }
        }
        return startTime;
    }
}





