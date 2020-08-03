package com.sanchit.funda.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private static final Map<String, Cache> _caches = new HashMap<>();
    private static final Map<String, Object> _raw_caches = new HashMap<>();

    public static final void registerRawData(String cacheName, Object data) {
        _raw_caches.put(cacheName, data);
    }

    public static final void registerCache(String cacheName, Cache cache) {
        _caches.put(cacheName, cache);
    }

    public static final Object get(String cacheName) {
        if (_caches.containsKey(cacheName)) {
            return _caches.get(cacheName);
        }
        return _raw_caches.get(cacheName);
    }

    public static class Cache<K, V> {

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
    }
}