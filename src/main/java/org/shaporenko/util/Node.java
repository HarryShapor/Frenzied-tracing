package org.shaporenko.util;

public class Node<T> {
    T data;
    Node next;

    Node(T value) {
        this.data = value;
        this.next = null;
    }

    public Node(T data, Node next) {
        this.data = data;
        this.next = next;
    }
}
