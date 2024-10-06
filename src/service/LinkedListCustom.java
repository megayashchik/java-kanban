package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedListCustom<T> {

    private Node<Task> head;
    private Node<Task> tail;

    public static final Map<Integer, Node<Task>> taskNodeMap = new HashMap<>();

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);

        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        return newNode;
    }

    public List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            listOfTasks.add(node.task);
            node = node.next;
        }
        return listOfTasks;
    }

    public void removeNode(Node<Task> node) {
        if (node != null) {

            if (node == head) {
                head = head.next;
                if (head != null) {
                    head.prev = null;
                }
            } else {
                if (node.prev != null) {
                    node.prev.next = node.next;
                }
            }

            if (node == tail) {
                tail = tail.prev;
                if (tail != null) {
                    tail.next = null;
                }
            } else {
                if (node.next != null) {
                    node.next.prev = node.prev;
                }
            }
        }
    }

    public void add(Task task) {
        int taskId = task.getId();

        if (taskNodeMap.containsKey(taskId)) {
            removeNode(taskNodeMap.get(taskId));
        }
        taskNodeMap.put(taskId, linkLast(task));
    }

    public void remove(int id) {
        if (taskNodeMap.containsKey(id)) {
            removeNode(taskNodeMap.get(id));
            taskNodeMap.remove(id);
        }
    }
}


