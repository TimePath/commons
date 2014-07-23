package com.timepath.io;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;

public class BitBufferTest {

    @Test
    @Ignore("Known bug")
    public void testJitter() {
        BitBuffer scramble = new BitBuffer(ByteBuffer.wrap(new byte[] {
                (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
        }));
        int shift = 1;
        scramble.position(0, shift);
        int first = scramble.getByte();
        scramble.position(0, shift);
        int second = scramble.getByte();
        assertEquals(first, second);
    }

    @Test
    public void testShift() {
        int number = 1;
        String expected = Long.toBinaryString(number);
        int bitLength = expected.length();
        for(int i = 0; i < ( 32 - bitLength ); i++) {
            int n = number << i;
            ByteBuffer b = ByteBuffer.allocate(4);
            b.order(ByteOrder.LITTLE_ENDIAN);
            b.putInt(n);
            b.flip();
            BitBuffer bb = new BitBuffer(b);
            bb.getBits(i);
            long bits = bb.getBits(bitLength);
            assertEquals(expected, Long.toBinaryString(bits));
        }

    }

    @Test
    public void testBits() {
        int number = (int) ( Math.random() * Integer.MAX_VALUE );
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(number);
        b.flip();
        BitBuffer bb = new BitBuffer(b);
        String binaryString = Integer.toBinaryString(number);
        String test = "";
        for(int i = 0; i < binaryString.length(); i++) {
            test = bb.getBits(1) + test;
        }
        assertEquals(binaryString, test);
    }
}
