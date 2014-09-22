package com.timepath.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class BitBuffer {

    /**
     * Total number of bits
     */
    private final int capacityBits;
    /**
     * The source of data
     */
    private final ByteBuffer source;
    /**
     * Internal field holding the current byte in the source buffer
     */
    private byte b;
    /**
     * Position in bits
     */
    private int position;

    public BitBuffer(ByteBuffer bytes) {
        source = bytes;
        capacityBits = source.capacity() * 8;
    }

    /**
     * @return the capacity in bytes
     */
    public int capacity() {
        return capacityBits / 8;
    }

    public void get(byte[] dst) {
        get(dst, 0, dst.length);
    }

    public void get(byte[] dst, int offset, int length) {
        for (int i = offset; i < (offset + length); i++) {
            dst[i] = getByte();
        }
    }

    /**
     * Loads source data into internal byte.
     */
    protected void nextByte() {
        b = source.get();
    }

    public long getBits(int n) {
        if (n == 0) return 0;
        long data = 0;
        for (int i = 0; i < n; i++) {
            int bitOffset = position++ % 8; // Bit offset in current byte
            if (bitOffset == 0) nextByte(); // Fill byte on boundary read
            int m = 1 << bitOffset; // Generate mask
            if ((b & m) != 0) data |= (1 << i); // Copy bit
        }
        return data;
    }

    public boolean getBoolean() {
        return getBits(1) != 0;
    }

    public byte getByte() {
        return (byte) getBits(8);
    }

    public short getShort() {
        return (short) getBits(16);
    }

    public int getInt() {
        return (int) getBits(32);
    }

    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    public long getLong() {
        return getBits(64);
    }

    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    /**
     * @param limit never read more than this many bytes
     * @param exact always read to the limit
     */
    public String getString(int limit, boolean exact) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte c; baos.size() != limit && (c = getByte()) != 0; ) baos.write(c);
        if (exact && limit > 0) get(new byte[limit - baos.size()]); // Read and discard the remainder
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * @param limit never read more than this many bytes
     */
    public String getString(int limit) {
        return getString(limit, false);
    }

    /**
     * Reads a String up to the first terminating null byte ('\0')
     */
    public String getString() {
        return getString(-1);
    }

    /**
     * @return true if more than 1 byte is available
     */
    public boolean hasRemaining() {
        return remaining() > 0;
    }

    /**
     * @return the number of remaining bytes
     */
    public int remaining() {
        return remainingBits() / 8;
    }

    /**
     * @return the number of remaining bits
     */
    public int remainingBits() {
        return capacityBits - position;
    }

    /**
     * @return true if more than 1 bit is available
     */
    public boolean hasRemainingBits() {
        return remainingBits() > 0;
    }

    /**
     * @return the limit in bytes
     */
    public int limit() {
        return capacityBits / 8;
    }

    /**
     * Does nothing.
     */
    public void order(ByteOrder bo) {
    }

    /**
     * Sets the position.
     *
     * @param newPosition the new position
     */
    public void position(int newPosition) {
        position(newPosition, 0);
    }

    /**
     * Sets the position.
     *
     * @param newPosition the byte offset
     * @param bits        the bit offset
     */
    public void position(int newPosition, int bits) {
        source.position(newPosition);
        position = newPosition * 8; // Set byte position
        getBits(bits); // Read extra bits manually
    }

    /**
     * @return the position in bytes
     */
    public int position() {
        return position / 8;
    }

    /**
     * @return the position in bits
     */
    public int positionBits() {
        return position;
    }
}
