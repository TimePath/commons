package com.timepath.io;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class BitBuffer {

    private static final Logger LOG = Logger.getLogger(BitBuffer.class.getName());

    private static final int[] masks = new int[] {0, 1, 2, 4, 8, 16, 32, 64, 128};

    private byte b;

    private int left = 0;

    private int remainingBits;

    private final ByteBuffer source;

    public BitBuffer(ByteBuffer bytes) {
        this.source = bytes;
        this.remainingBits = source.remaining() * 8;
    }

    public long getBits(int bits) {
        long data = 0;
        for(int i = 0; i < bits; i++) {
            if(left == 0) {
                nextByte();
            }
            if((b & masks[left]) != 0) {
                data |= masks[left];
            }
            left--;
        }
        remainingBits -= bits;
        return data;
    }

    public boolean getBoolean() {
        return getBits(1) != 0;
    }

    public byte getByte() {
        return (byte) getBits(8);
    }

    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    public int getInt() {
        return (int) getBits(32);
    }

    public long getLong() {
        return getBits(64);
    }

    public short getShort() {
        return (short) getBits(16);
    }

    public int position() {
        return source.position();
    }

    public int remaining() {
        return source.remaining();
    }

    public int remainingBits() {
        return remainingBits;
    }

    private void nextByte() {
        int end = source.limit();
        source.limit(source.position() + 1);
        b = source.slice().get();
        source.limit(end);
        left = 8;
    }

}
