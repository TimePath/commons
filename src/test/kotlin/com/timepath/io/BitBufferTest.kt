package com.timepath.io

import org.junit.Assert.assertEquals
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Test as test

public class BitBufferTest {

    test fun testJitter() {
        val scramble = BitBuffer(ByteBuffer.wrap(byteArray(202.toByte(), 254.toByte(), 186.toByte(), 190.toByte())))
        val shift = 1
        scramble.position(0, shift)
        val first = scramble.getByte().toInt()
        scramble.position(0, shift)
        val second = scramble.getByte().toInt()
        assertEquals(first.toLong(), second.toLong())
    }

    test fun testShift() {
        val number = 1
        val expected = java.lang.Long.toBinaryString(number.toLong())
        val bitLength = expected.length()
        for (i in 0..(32 - bitLength) - 1) {
            val n = number shl i
            val b = ByteBuffer.allocate(4)
            b.order(ByteOrder.LITTLE_ENDIAN)
            b.putInt(n)
            b.flip()
            val bb = BitBuffer(b)
            bb.getBits(i)
            val bits = bb.getBits(bitLength)
            assertEquals(expected, java.lang.Long.toBinaryString(bits))
        }

    }

    test fun testBits() {
        val number = (Math.random() * Integer.MAX_VALUE.toDouble()).toInt()
        val b = ByteBuffer.allocate(4)
        b.order(ByteOrder.LITTLE_ENDIAN)
        b.putInt(number)
        b.flip()
        val bb = BitBuffer(b)
        val binaryString = Integer.toBinaryString(number)
        var test = ""
        for (i in 0..binaryString.length() - 1) {
            test = bb.getBits(1).toString() + test
        }
        assertEquals(binaryString, test)
    }
}
