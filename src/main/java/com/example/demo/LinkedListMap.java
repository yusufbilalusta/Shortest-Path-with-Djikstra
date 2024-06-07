package com.example.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LinkedListMap<K, V> {
    private Node<K, V> head;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    public LinkedListMap() {
        this.head = null;
    }

    //Key ile istenen değeri donmesi için get metodu
    public V get(K key) {
        Node<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /*
    Eğer map içerisinde belirtilen key var ise o keye denk düşen Valueyu koyar
    Yada Eğer bu key yoksa en öne bu keyi koyar
    Yani yeni node head olur
     */
    public void put(K key, V value) {
        Node<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = head;
        head = newNode;
    }

    public boolean containsKey(K key) {
        Node<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    //Setin özelliği burda anahtarları saklayan bir liste görevi göörüyoru
    //Anahtarların benzersiz olması gerektiği için Set kullanıldı
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        Node<K, V> current = head;
        while (current != null) {
            keySet.add(current.key);
            current = current.next;
        }
        return keySet;
    }

    //Değerlerin benzersiz olması şart olmadığı için ArrayList ile sakladık
    public List<V> values() {
        List<V> valuesList = new ArrayList<>();
        Node<K, V> current = head;
        while (current != null) {
            valuesList.add(current.value);
            current = current.next;
        }
        return valuesList;
    }
}