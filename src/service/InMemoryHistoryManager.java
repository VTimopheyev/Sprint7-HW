package service;

import issues.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> tasksLinks;


    public InMemoryHistoryManager() {
        this.tasksLinks = new CustomLinkedList<>();
    }

    public void add(Task task, int id) {
        tasksLinks.linkLast(task, id);
    }

    public void removeTaskFromHistory(Task task, int id) {
        if (tasksLinks.tasksNodes.containsKey(id)) {
            tasksLinks.removeNode(tasksLinks.tasksNodes.get(id), id);
        }
    }


    public List<Task> getHistory() {

        return tasksLinks.getTasks();
    }



    public class CustomLinkedList<Task> {

        private HashMap<Integer, Node> tasksNodes = new HashMap<>();
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public void linkLast(Task data, int id) {
            if (tasksNodes.containsKey(id)) {
                Node node = tasksNodes.get(id);
                removeNode(node, id);
                tasksNodes.remove(id);
            }

            if (tasksLinks.size >= 1) {
                Node<Task> newNode = new Node<>(tail, data, null);
                newNode.prev.next = newNode;
                tail = newNode;
                tasksNodes.put(id, newNode);
            } else if (tasksLinks.size == 0) {
                Node newNode = new Node<>(null, data, null);
                head = newNode;
                tail = newNode;
                tasksNodes.put(id, newNode);
            }
            size++;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node node = head;
            while (node != null) {
                tasks.add((Task) node.data);
                node = node.next;
            }
            return tasks;
        }

        public void removeNode(Node node, int id) {

            if (tasksLinks.size == 0) {
                return;
            } else if (tasksLinks.size == 1) {
                head = null;
                tail = null;
                tasksLinks.size--;
                return;
            } else if (tasksLinks.size > 1) {
                if (node.next == null) {
                    node.prev.next = null;
                    tail = node.prev;
                } else if (node.prev == null) {
                    node.next.prev = null;
                    head = node.next;
                } else {
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                }
                size--;
                tasksNodes.remove(id);
            }
        }
    }
}
