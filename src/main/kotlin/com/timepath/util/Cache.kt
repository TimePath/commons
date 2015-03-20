package com.timepath.util

import java.util.HashMap

/**
 * Wraps access to an existing Map.
 *
 * @param delegate the existing map
 */
public abstract class Cache<K : Any, V : Any>(delegate: MutableMap<K, V> = HashMap()) : Map<K, V>, MutableMap<K, V> {

    public fun getBackingMap(): Map<K, V> = m

    private var m: MutableMap<K, V> = delegate

    /**
     * Called in response to accessing an undefined key. Gives an opportunity to lazily initialize.
     *
     * @param key the key
     * @return the value to fill
     */
    protected abstract fun fill(key: K): V?

    /**
     * Called in response to accessing a key to check if it has expired. The default implementation never expires.
     *
     * @param key   the key
     * @param value the current value
     * @return null if the key has expired. If the value really is null, return it from {@link #fill}.
     */
    protected open fun expire(key: K, value: V?): V? = value

    override fun size(): Int = m.size()

    override fun isEmpty(): Boolean = m.isEmpty()

    override fun containsKey(key: Any?): Boolean = get(key) != null

    override fun containsValue(value: Any?): Boolean = m.containsValue(value)

    synchronized override fun get(key: Any?): V? {
        try {
            [suppress("UNCHECKED_CAST")]
            val k = key as K
            val expire = expire(k, m[k])
            if (expire == null) {
                val fill = fill(k)
                if (fill != null) {
                    m[k] = fill
                    return fill
                } else {
                    m.remove(k)
                    return null
                }
            }
        } catch (ignored: ClassCastException) {
        }
        return null
    }

    override fun put(key: K, value: V): V? = m.put(key, value)

    override fun remove(key: Any?): V? = m.remove(key)

    override fun putAll(m: Map<out K, V>) = this.m.putAll(m)

    override fun clear() = m.clear()

    override fun keySet(): MutableSet<K> = m.keySet()

    override fun values(): MutableCollection<V> = m.values()

    override fun entrySet(): MutableSet<MutableMap.MutableEntry<K, V>> = m.entrySet()
}
