package com.example.demo;

import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.NoSuchElementException;


/*
    Iterable ı implement ederek next, Iterator arayüzünün fonksiyonlarını kullanmayı sağladık
    Iterator arayüzünde next ve hasnext metodları var.
    Ayrıca Queunun frontu QueueIteratore bağlı
 */

public class Queue<T> implements Iterable<T> {
    private NodeQueue<T> front;
    private NodeQueue<T> rear;
    private int size;

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public void add(T data) {
        NodeQueue<T> newNodeQueue = new NodeQueue<>(data);
        if (rear != null) {
            rear.next = newNodeQueue;
        }
        rear = newNodeQueue;
        if (front == null) {
            front = newNodeQueue;
        }
        size++;
    }


    //Sadece en ondeki elemanı kaldırır
    public T remove() {
        if (front == null) {
            throw new NoSuchElementException("Queue is empty");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    //Aranılan elemanı kaldırır
    public boolean remove(T data) {
        if (front == null) {
            return false;
        }

        if (front.data.equals(data)) {
            front = front.next;
            if (front == null) {
                rear = null;
            }
            size--;
            return true;
        }

        NodeQueue<T> current = front;
        while (current.next != null && !current.next.data.equals(data)) {
            current = current.next;
        }

        if (current.next == null) {
            return false;
        }
        //Eger kaldırılacak eleman bir sonraki elemansa onun bağını kaybediyoruz next bağlantısını bir sonrakine atarark

        current.next = current.next.next;
        if (current.next == null) {
            rear = current;
        }
        size--;
        return true;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public T peek() {
        if (front == null) {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.data;
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }


    /*
    Bir sonraki elemanın sırasıyla alınması için Iterator sınınıfı oluşturulmuştur.
     */
    private class QueueIterator implements Iterator<T> {
        private NodeQueue<T> current = front;

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
            current = current.next;
            return data;
        }
    }
    public class NodeQueue<T> {
        T data;
        NodeQueue<T> next;

        public NodeQueue(T data) {
            this.data = data;
            this.next = null;
        }
    }
}
