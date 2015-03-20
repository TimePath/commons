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
     * Internal field holding the current byte in the source buffer
     */
    private var b: Byte = 0
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

    /**
     * Loads source data into internal byte.
     */
    protected fun nextByte() {
        b = source.get()
    }

    public fun getBits(n: Int): Long {
        if (n == 0) return 0
        var data: Long = 0
        for (i in 0..n - 1) {
            val bitOffset = position++ % 8 // Bit offset in current byte
            if (bitOffset == 0) nextByte() // Fill byte on boundary read
            val m = 1 shl bitOffset // Generate mask
            if ((b.toInt() and m) != 0) data = data or (1 shl i).toLong() // Copy bit
        }
        return data
    }

    public fun getBoolean(): Boolean = getBits(1) != 0L

    public fun getByte(): Byte = getBits(8).toByte()

    public fun getShort(): Short = getBits(16).toShort()

    public fun getInt(): Int = getBits(32).toInt()

    public fun getFloat(): Float = java.lang.Float.intBitsToFloat(getInt())

    public fun getLong(): Long = getBits(64)

    public fun getDouble(): Double = java.lang.Double.longBitsToDouble(getLong())

    /**
     * @param limit never read more than this many bytes
     * @param exact always read to the limit
     */
    public fun getString(limit: Int, exact: Boolean): String {
        val baos = ByteArrayOutputStream()
        while (baos.size() != limit) {
            val c = getByte().toInt()
            if (c == 0) break
            baos.write(c)
        }
        if (exact && limit > 0) get(ByteArray(limit - baos.size())) // Read and discard the remainder
        return String(baos.toByteArray(), StandardCharsets.UTF_8)
    }

    /**
     * @param limit never read more than this many bytes
     */
    public fun getString(limit: Int): String = getString(limit, false)

    /**
     * Reads a String up to the first terminating null byte ('\0')
     */
    public fun getString(): String = getString(-1)

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
    public fun order([suppress("UNUSED_PARAMETER")] bo: ByteOrder): Unit = Unit

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
