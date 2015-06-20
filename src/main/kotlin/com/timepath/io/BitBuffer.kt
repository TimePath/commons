package com.timepath.io


import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

public class BitBuffer(
        /** The source of data */
        private val source: ByteBuffer
) {

    /**
     * Total number of bits
     */
    private val capacityBits: Int = source.capacity() * 8

    /**
     * Position in bits
     */
    private var position: Int = 0

    /**
     * @return the capacity in bytes
     */
    public fun capacity(): Int = capacityBits / 8

    public fun get(dst: ByteArray): Unit = get(dst, 0, dst.size())

    public fun get(dst: ByteArray, offset: Int, length: Int) {
        for (i in offset..(offset + length) - 1) {
            dst[i] = getByte()
        }
    }

    /** Copy of current byte in the source buffer */
    private var b: Byte = 0
    private var bPos: Int = 0
    private fun next() {
        b = source.get()
        bPos = position / 8
    }

    @suppress("NOTHING_TO_INLINE") inline
    private fun Byte.get(n: Int) = toInt() and (1 shl n) != 0

    @suppress("NOTHING_TO_INLINE") inline
    private fun Long.get(n: Int) = this and (1L shl n) != 0L

    @suppress("NOTHING_TO_INLINE") inline
    private fun Byte.withBit(n: Int, b: Boolean) = when {
        b -> (toInt() or (1 shl n)).toByte()
        else -> (toInt() and (1 shl n).inv()).toByte()
    }

    @suppress("NOTHING_TO_INLINE") inline
    private fun Long.withBit(n: Int, b: Boolean) = when {
        b -> (this or (1L shl n))
        else -> (this and (1L shl n).inv())
    }

    public fun getBits(n: Int): Long {
        if (n == 0) return 0
        var data = 0L
        repeat(n) {
            val i = position++ % 8 // Bit offset in current byte
            if (i == 0) next() // Fill byte on boundary read
            data = data.withBit(it, b[i])
        }
        return data
    }

    public fun putBits(n: Int, data: Long) {
        if (n == 0) return
        repeat(n) {
            val i = position++ % 8 // Bit offset in current byte
            if (i == 0) next() // Fill byte on boundary read
            b = b.withBit(i, data[it])
            source.put(bPos, b)
        }
    }

    public fun getBoolean(): Boolean = getBits(1) != 0L
    public fun putBoolean(b: Boolean): Boolean = putBits(1, if (b) 1 else 0) let { b }

    public fun getByte(): Byte = getBits(8).toByte()
    public fun putByte(b: Byte): Byte = putBits(8, b.toLong()) let { b }

    public fun getShort(): Short = getBits(16).toShort()
    public fun putShort(s: Short): Short = putBits(16, s.toLong()) let { s }

    public fun getInt(): Int = getBits(32).toInt()
    public fun putInt(i: Int): Int = putBits(32, i.toLong()) let { i }

    public fun getFloat(): Float = java.lang.Float.intBitsToFloat(getInt())
    public fun putFloat(f: Float): Float = putInt(java.lang.Float.floatToIntBits(f)) let { f }

    public fun getLong(): Long = getBits(64)
    public fun putLong(l: Long): Long = putBits(64, l) let { l }

    public fun getDouble(): Double = java.lang.Double.longBitsToDouble(getLong())
    public fun putDouble(d: Double): Double = putLong(java.lang.Double.doubleToLongBits(d)) let { d }

    /**
     * @param limit never read more than this many bytes. If -1, up to the first terminating null byte ('\0')
     * @param exact always read to the limit
     */
    public fun getString(limit: Int = -1, exact: Boolean = false): String {
        val baos = ByteArrayOutputStream()
        while (baos.size() != limit) {
            val c = getByte().toInt()
            if (c == 0) break
            baos.write(c)
        }
        if (exact && limit > 0) get(ByteArray(limit - baos.size())) // Read and discard the remainder
        return String(baos.toByteArray(), StandardCharsets.UTF_8)
    }

    public fun putString(s: String): String {
        for (char in s) {
            putByte(char.toByte())
        }
        putByte(0)
        return s
    }

    /**
     * @return true if more than 1 byte is available
     */
    public fun hasRemaining(): Boolean = remaining() > 0

    /**
     * @return the number of remaining bytes
     */
    public fun remaining(): Int = remainingBits() / 8

    /**
     * @return the number of remaining bits
     */
    public fun remainingBits(): Int = capacityBits - position

    /**
     * @return true if more than 1 bit is available
     */
    public fun hasRemainingBits(): Boolean = remainingBits() > 0

    /**
     * @return the limit in bytes
     */
    public fun limit(): Int = capacityBits / 8

    /**
     * Does nothing.
     */
    public fun order(@suppress("UNUSED_PARAMETER") bo: ByteOrder): Unit = Unit

    /**
     * Sets the position.
     *
     * @param newPosition the new position
     */
    public fun position(newPosition: Int): Unit = position(newPosition, 0)

    /**
     * Sets the position.
     *
     * @param newPosition the byte offset
     * @param bits        the bit offset
     */
    public fun position(newPosition: Int, bits: Int) {
        source.position(newPosition)
        position = newPosition * 8 // Set byte position
        getBits(bits) // Read extra bits manually
    }

    /**
     * @return the position in bytes
     */
    public fun position(): Int = position / 8

    /**
     * @return the position in bits
     */
    public fun positionBits(): Int = position
}
