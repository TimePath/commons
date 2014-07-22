package com.timepath.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class BitBuffer {

    private static final Logger LOG = Logger.getLogger(BitBuffer.class.getName());
    /** Total number of bits */
    private final int        capacityBits;
    /** The source of data */
    private final ByteBuffer source;
    /** Internal field holding the current byte in the source buffer */
    private       short      b;
    /** Position in bits */
    private       int        position;
    /** Stores bit access offset */
    private       int        positionBit;
    /** Internal field holding the remaining bits in the current byte */
    private       int        remainingBits;

    public BitBuffer(ByteBuffer bytes) {
        source = bytes;
        capacityBits = source.capacity() * 8;
    }

    public int capacity() { return capacityBits / 8; }

    public void get(byte[] dst) { get(dst, 0, dst.length); }

    public void get(byte[] dst, int offset, int length) {
        for(int i = offset; i < ( offset + length ); i++) { dst[i] = getByte(); }
    }

    /** Loads source data into internal byte. */
    protected void nextByte() {
        b = (short) ( source.get() & 0xFF );
        remainingBits = 8;
    }

    public long getBits(int n) {
        long data = 0;
        for(int i = 0; i < n; i++) {
            if(remainingBits == 0) nextByte();
            remainingBits--;
            int m = 1 << ( positionBit++ % 8 );
            if(( b & m ) != 0) {
                data |= 1 << i;
            }
        }
        position += n;
        return data;
    }

    public boolean getBoolean() { return getBits(1) != 0; }

    public byte getByte() { return (byte) getBits(8); }

    public short getShort() { return (short) getBits(16); }

    public int getInt() { return (int) getBits(32); }

    public float getFloat() { return Float.intBitsToFloat(getInt()); }

    public long getLong() { return getBits(64); }

    public double getDouble() { return Double.longBitsToDouble(getLong()); }

    public String getString(int limit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(byte c; ( c = getByte() ) != 0; ) baos.write(c);
        if(limit > 0) get(new byte[limit - baos.size()]);
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(baos.toByteArray())).toString();
    }

    public String getString() { return getString(0); }

    /** @return true if more than 1 byte is available */
    public boolean hasRemaining() { return remaining() > 0; }

    /** @return the number of remaining bytes */
    public int remaining() { return remainingBits() / 8; }

    /** @return the number of remaining bits */
    public int remainingBits() { return capacityBits - position; }

    /** @return true if more than 1 bit is available */
    public boolean hasRemainingBits() { return remainingBits() > 0; }

    /** @return the limit in bytes */
    public int limit() { return capacityBits / 8; }

    /** Does nothing. */
    public void order(ByteOrder bo) { }

    /**
     * Sets the position.
     *
     * @param newPosition
     *         the new position
     */
    public void position(int newPosition) { position(newPosition, 0); }

    /**
     * Sets the position.
     *
     * @param newPosition
     *         the byte offset
     * @param bits
     *         the bit offset
     */
    public void position(int newPosition, int bits) {
        source.position(newPosition);
        position = newPosition * 8;
        positionBit = bits;
        remainingBits = 0;
    }

    /** @return the position in bytes */
    public int position() { return positionBits() / 8; }

    /** @return the position in bits */
    public int positionBits() { return position; }
}
