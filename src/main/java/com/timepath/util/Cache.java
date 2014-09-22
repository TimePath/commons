package com.timepath.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TimePath
 */
public abstract class Cache<K, V> implements Map<K, V> {

    private Map<K, V> m = new HashMap<K, V>();

    public Cache() {
        m = new HashMap<>();
    }

    /**
     * Wraps access to an existing Map.
     *
     * @param delegate the existing map
     */
    public Cache(Map<K, V> delegate) {
        m = delegate;
    }

    /**
     * Called in response to accessing an undefined key. Gives an opportunity to lazily initialize.
     *
     * @param key the key
     * @return the value to fill
     */
    protected abstract V fill(K key);

    /**
     * Called in response to accessing a key to check if it has expired. The default implementation never expires.
     *
     * @param key the key
     * @return true if the key has expired
     */
    protected boolean expire(K key) {
        return false;
    }

    @Override
    public int size() {
        return m.size();
    }

    @Override
    public boolean isEmpty() {
        return m.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return m.containsValue(value);
    }

    @Override
    public synchronized V get(Object keyObject) {
        V value = null;
        try {
            @SuppressWarnings("unchecked") K key = (K) keyObject;
            value = m.get(key);
            if (value == null || expire(key)) {
                value = fill(key);
                if (value != null) m.put(key, value);
            }
        } catch (ClassCastException ignored) {
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        return m.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return m.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.m.putAll(m);
    }

    @Override
    public void clear() {
        m.clear();
    }

    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @Override
    public Collection<V> values() {
        return m.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return m.entrySet();
    }
}
