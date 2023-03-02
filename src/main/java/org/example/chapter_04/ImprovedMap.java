package org.example.chapter_04;


import java.util.HashMap;
import java.util.Map;

public class ImprovedMap<K, V> extends HashMap<K, V> {
    private final Map<K, V> map = new HashMap<>();

    @Override
    public V put(K key, V value) {
        synchronized (map) {
            return map.put(key, value);
        }
    }

    @Override
    public int size() {
        synchronized (map) {
            return map.size();
        }
    }
}
