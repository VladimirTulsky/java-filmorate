package managers;

import taskss.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId() + 1;
        linkLast(task);
        nodeMap.put(id, last);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }


    private void removeNode(Node node) {
        if (node == null) {
            return;
        } else {
            if (node.next != null && node.prev != null) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            if (node.next == null && node.prev != null) {
                node.prev.next = null;
                last = node.prev;
            }
            if (node.next != null && node.prev == null) {
                node.next.prev = null;
                first = node.next;
            }
            if (node.next == null && node.prev == null) {
                first = null;
                last = null;
            }
        }
    }

    public void linkLast(Task task) {
        Node node = new Node(task, null, last);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    private Node first;
    private Node last;

    static class Node {
        private Task data;
        private Node next;
        private Node prev;

        public Node(Task data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }


    }
}

