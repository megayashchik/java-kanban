package service;

public class Node<T> {

    public T task;
    public Node<T> prev;
    public Node<T> next;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }
}
