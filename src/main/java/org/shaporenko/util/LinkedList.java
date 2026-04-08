package org.shaporenko.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private int size = 0;
    public int length = 0;
    private Node<T> current;

    public LinkedList() {
        this.head = null;
        this.size = 0;
        this.length = 0;
    }

    public void ins(T value, int position) {
        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException("Позиция вне границ");
        }
        Node newNode = new Node(value);
        if (position == 0) {
            newNode.next = this.head;
            this.head = newNode;
        } else {
            Node current = this.head;
            for (int i = 0; i < position - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        this.size++;
        this.length++;
    }

    public void ins(T value) {

        Node newNode = new Node(value);
        if (size == 0) {
            newNode.next = this.head;
            this.head = newNode;
            this.current = newNode;
        } else {
//            Node current = this.head;
//            for (int i = 0; i < size - 1; i++) {
//                current = current.next;
//            }
            newNode.next = this.current.next;
            this.current.next = newNode;
        }
        this.size++;
        this.length++;
    }

    public boolean del(T value) {
        if (this.head == null) return false;

        if (this.head.data.equals(value)) {
            this.head = this.head.next;
            this.size--;
            this.length--;
            return true;
        }

        Node current = this.head;
        while (current.next != null && !current.next.data.equals(value)) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
            this.size--;
            this.length--;
            return true;
        }
        return false;
    }

    public T ret(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Индекс вне границ");
        }

        Node current = this.head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return (T) current.data;
    }

    public int locate(T value) {
        Node current = this.head;
        int position = 0;

        while (current != null) {
            if (current.data.equals(value)) {
                return position;
            }
            current = current.next;
            position++;
        }

        return -1;
    }

    public void makenull() {
        this.head = null;
        this.size = 0;
        this.length = 0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (T element : this){
            stringBuilder.append(element.toString() + ", ");
        }
        return stringBuilder.toString();
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head;
        private Node<T> previous = null;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T data = current.data;
            previous = current;
            current = current.next;

            return data;
        }
    }
}