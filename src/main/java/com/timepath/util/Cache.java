package com.timepath.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TimePath
 */
public abstract class Cache<K, V> implements Map<K, V> {

    public Map<K, V> getBackingMap() {
        return m;
    }

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
    @Nullable
    protected abstract V fill(K key);

    /**
     * Called in response to accessing a key to check if it has expired. The default implementation never expires.
     *
     * @param key   the key
     * @param value the current value
     * @return null if the key has expired. If the value really is null, return it from {@link #fill}.
     */
    @Nullable
    protected V expire(K key, V value) {
        return value;
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
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return m.containsValue(value);
    }

    @Nullable
    @Override
    public synchronized V get(Object key) {
        @Nullable V v = null;
        try {
            @NotNull @SuppressWarnings("unchecked") K k = (K) key;
            if ((v = expire(k, m.get(k))) == null) {
                m.put(k, v = fill(k));
            }
        } catch (ClassCastException ignored) {
        }
        return v;
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
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        this.m.putAll(m);
    }

    @Override
    public void clear() {
        m.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return m.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return m.entrySet();
    }
}
