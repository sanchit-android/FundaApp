package com.sanchit.funda.cache;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CacheManager {

    private static final Map<String, Cache> _caches = new HashMap<>();
    private static final Map<String, Object> _raw_caches = new HashMap<>();

    public static final Object registerRawData(String cacheName, Object data) {
        _raw_caches.put(cacheName, data);
        return data;
    }

    public static final <Z> Cache<String, Z> registerCache(String cacheName, Cache<String, Z> cache) {
        _caches.put(cacheName, cache);
        return cache;
    }

    public static final Object get(String cacheName) {
        if (_caches.containsKey(cacheName)) {
            return _caches.get(cacheName);
        }
        return _raw_caches.get(cacheName);
    }

    public static final <Z> Cache<String, Z> get(String cacheName, Class<Z> zClass) {
        return (Cache<String, Z>) _caches.get(cacheName);
    }

    public static final <Z> Cache<String, Z> getOrRegisterCache(String cacheName, Class<Z> zClass) {
        if (_caches.containsKey(cacheName)) {
            return (Cache<String, Z>) _caches.get(cacheName);
        }
        Cache<String, Z> cache = new Cache<String, Z>();
        _caches.put(cacheName, cache);
        return cache;
    }

    public static boolean hasCache(String cacheName) {
        if (_caches.containsKey(cacheName)) {
            return true;
        }
        return _raw_caches.containsKey(cacheName);
    }

    public static class Cache<K, V> implements Iterable<V> {

        private final Map<K, V> _cache = new HashMap<>();

        public V add(K key, V value) {
            return _cache.put(key, value);
        }

        public V get(K key) {
            return _cache.get(key);
        }

        public boolean exists(K key) {
            return _cache.containsKey(key);
        }

        public V clear(K key) {
            V value = get(key);
            _cache.remove(key);
            return value;
        }

        public void clear() {
            _cache.clear();
        }

        @NonNull
        @Override
        public Iterator<V> iterator() {
            return _cache.values().iterator();
        }
    }
}
