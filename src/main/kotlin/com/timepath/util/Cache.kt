package com.timepath.util

public open class Cache<K : Any, V : Any>(
        private val delegate: MutableMap<K, V> = hashMapOf(),
        fill: (K) -> V? = { null }
) : MutableMap<K, V> by delegate {

    public val backingMap: Map<K, V> get() = delegate

    private val f = fill

    /**
     * Called in response to accessing an undefined key. Gives an opportunity to lazily initialize.
     *
     * @param key the key
     * @return the value to fill
     */
    protected open fun fill(key: K): V? = f(key)

    /**
     * Called in response to accessing a key to check if it has expired. The default implementation never expires.
     *
     * @param key the key
     * @param value the current value
     * @return null if the key has expired. If the value really is null, return it from [fill].
     */
    protected open fun expire(key: K, value: V?): V? = value

    override fun containsKey(key: K) = get(key) != null

    @Synchronized override fun get(key: K): V? {
        val got = delegate[key]
        val expire = expire(key, got)
        if (expire != null) return got
        val fill = fill(key)
        if (fill != null) {
            delegate[key] = fill
            return fill
        } else {
            delegate.remove(key)
            return null
        }
    }
}
