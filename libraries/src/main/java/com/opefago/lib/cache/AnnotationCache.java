package com.opefago.lib.cache;

public interface AnnotationCache<K, V> {
    void put(K key, V value);
    V get(K key);
    V evict(K key);
}
