package com.timepath.io;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

public class BitBufferTest {

    private static final Logger LOG = Logger.getLogger(BitBufferTest.class.getName());

    @Test
    public void testShift() {
        BitBuffer scramble = new BitBuffer(ByteBuffer.wrap(new byte[] {
                (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
        }));
        int shift = 1;
        scramble.position(0, shift);
        int first = scramble.getByte();
        scramble.position(0, shift);
        int second = scramble.getByte();
        assert first == second;
        LOG.info(first + " vs " + second);
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
            LOG.info(Long.toBinaryString(bits) + " == " + expected + " ?");
        }
        number = (int) ( Math.random() * Integer.MAX_VALUE );
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
        LOG.info(s1);
        LOG.info(s2);
    }
}
