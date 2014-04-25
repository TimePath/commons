package com.timepath.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class BitBuffer {

    private static final Logger LOG = Logger.getLogger(BitBuffer.class.getName());

    public static void main(String[] args) {
        int number = 1;
        String expected = Long.toBinaryString(number);
        int bitLength = expected.length();

        for(int i = 0; i < 32 - bitLength; i++) {
            int n = number << i;
            ByteBuffer b = ByteBuffer.allocate(4);
            b.order(ByteOrder.LITTLE_ENDIAN);
            b.putInt(n);
            b.flip();
            BitBuffer bb = new BitBuffer(b);
            bb.getBits(i);
            long bits = bb.getBits(bitLength);
            System.out.println(Long.toBinaryString(bits) + " == " + expected + " ?");
        }

        number = (int) (Math.random() * Integer.MAX_VALUE);
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(number);
        b.flip();
        BitBuffer bb = new BitBuffer(b);
        String s1 = Integer.toBinaryString(number);
        String s2 = "";
        for(int i = 0; i < s1.length(); i++) {
            s2 = bb.getBits(1) + s2;
        }
        System.out.println(s1);
        System.out.println(s2);
    }

    private short b;

    private int left = 0;

    /**
     * Stores bit offset
     */
    private int mask = 0;

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
            left--;
            int m = (1 << (mask++ % 8));
            if((b & m) != 0) {
                data |= 1 << i;
            }
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

    @SuppressWarnings("empty-statement")
    public String getString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(byte c; (c = getByte()) != 0; baos.write(c));
        return Charset.forName("UTF-8").decode(ByteBuffer.wrap(baos.toByteArray())).toString();
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
        b = (short) (source.get() & 0xFF);
        left = 8;
    }

}
