package com.timepath.io.struct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StructTest {

    @NotNull
    byte[] expect = {
            8, 0, 1, 0, 2, 0, 3, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 5, 64, -64, 0, 0, 64, 28, 0, 0, 0, 0, 0, 0
    };

    @Test
    public void testSizeof() throws Exception {
        assertEquals(expect.length, Struct.sizeof(new Example()));
        System.out.println("sized");
    }

    @Test
    public void testPack() throws Exception {
        assertArrayEquals(expect, Struct.pack(new Example()));
        System.out.println("packed");
    }

    @Test
    public void testUnpack() throws Exception {
        @NotNull Example out = new Example();
        boolean before = out.aBoolean;
        out.aBoolean = !before;
        Struct.unpack(out, expect);
        boolean after = out.aBoolean;
        assertEquals(before, after);
        System.out.println("unpacked");
    }

    @Test
    public void testArrayWrite() throws Exception {
        class Wrapper {

            @StructField
            byte[] buf;
        }
        @NotNull Wrapper w = new Wrapper();
        w.buf = new byte[]{1, 2, 3, 4, 5};
        @Nullable byte[] packed = Struct.pack(w);
        assertArrayEquals(w.buf, packed);
        System.out.println("arrays");
    }

    class Example {

        @StructField
        boolean aBoolean;
        @StructField
        byte aByte = 1;
        @StructField
        char aChar = 2;
        @StructField
        short aShort = 3;
        @StructField
        int anInt = 4;
        @StructField
        long aLong = 5;
        @StructField
        float aFloat = 6;
        @StructField
        double aDouble = 7;
        @NotNull
        @StructField(index = -1, limit = 1)
        String string = "\u0008";
    }
}
