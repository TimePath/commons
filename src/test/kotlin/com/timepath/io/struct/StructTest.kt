package com.timepath.io.struct

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

public class StructTest {

    var expect = byteArrayOf(8, 0, 1, 0, 2, 0, 3, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 5, 64, -64, 0, 0, 64, 28, 0, 0, 0, 0, 0, 0)

    @Test fun testSizeof() {
        assertEquals(expect.size().toLong(), Struct.sizeof(Example()).toLong())
        System.out.println("sized")
    }

    @Test fun testPack() {
        assertArrayEquals(expect, Struct.pack(Example()))
        System.out.println("packed")
    }

    @Test fun testUnpack() {
        val out = Example()
        val before = out.aBoolean
        out.aBoolean = !before
        Struct.unpack(out, *expect)
        val after = out.aBoolean
        assertEquals(before, after)
        System.out.println("unpacked")
    }

    @Test fun testArrayWrite() {
        class Wrapper {

            @StructField
            var buf: ByteArray? = null
        }

        val w = Wrapper()
        w.buf = byteArrayOf(1, 2, 3, 4, 5)
        val packed = Struct.pack(w)
        assertArrayEquals(w.buf, packed)
        System.out.println("arrays")
    }

    inner class Example {

        @StructField
        var aBoolean: Boolean = false
        @StructField
        var aByte: Byte = 1
        @StructField
        var aChar: Char = 2.toChar()
        @StructField
        var aShort: Short = 3
        @StructField
        var anInt = 4
        @StructField
        var aLong: Long = 5
        @StructField
        var aFloat: Float = 6f
        @StructField
        var aDouble: Double = 7.0
        @StructField(index = -1, limit = 1)
        var string = "\u0008"
    }
}
