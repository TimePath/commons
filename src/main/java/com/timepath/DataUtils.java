package com.timepath;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class DataUtils {

    /**
     * Conversion table:
     * C Java
     * byte 8 8
     * char 8 16
     * short 16 16
     * int 16 32
     * long 32 64
     * float 32 32
     */
    private static final Logger LOG = Logger.getLogger(DataUtils.class.getName());

    private DataUtils() {
    }

    /**
     * Defensive copy
     *
     * @param source
     * @return
     */
    @NotNull
    public static ByteBuffer getSlice(@NotNull ByteBuffer source) {
        return getSlice(source, source.remaining());
    }

    /**
     * Reads length bytes ahead, turns them into a ByteBuffer, and then jumps there
     *
     * @param source
     * @param length
     * @return
     */
    @NotNull
    public static ByteBuffer getSlice(@NotNull ByteBuffer source, int length) {
        int originalLimit = source.limit();
        source.limit(source.position() + length);
        @NotNull ByteBuffer sub = source.slice();
        source.position(source.limit());
        source.limit(originalLimit);
        sub.order(ByteOrder.LITTLE_ENDIAN);
        return sub;
    }

    @NotNull
    public static ByteBuffer getSliceSafe(@NotNull ByteBuffer source, int length) {
        return getSlice(source, Math.min(length, source.remaining()));
    }

    public static ByteBuffer mapFile(@NotNull File f) throws IOException {
        @NotNull RandomAccessFile raf = new RandomAccessFile(f, "r");
        //        FileInputStream fis = new FileInputStream(f);
        FileChannel fc = raf.getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f.length());
        mbb.order(ByteOrder.LITTLE_ENDIAN);
        return mbb;
    }

    @NotNull
    public static String readEntire(@NotNull InputStream in) {
        return readEntire(in, "UTF-8");
    }

    @NotNull
    private static String readEntire(@NotNull InputStream in, @NotNull String encoding) {
        @NotNull Scanner s = new Scanner(in, encoding);
        s.useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @NotNull
    public static ByteBuffer mapInputStream(InputStream is) throws IOException {
        int bs = 8192;
        if (!(is instanceof BufferedInputStream)) {
            //            is = new BufferedInputStream(is);
            bs = is.available();
        }
        @NotNull ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        @NotNull byte[] data = new byte[bs];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        @NotNull ByteBuffer mbb = ByteBuffer.wrap(buffer.toByteArray());
        mbb.order(ByteOrder.LITTLE_ENDIAN);
        return mbb;
    }

    @NotNull
    public static String getString(@NotNull ByteBuffer source) {
        @NotNull ByteBuffer[] cloned = getTextBuffer(source.duplicate(), true);
        source.position(source.position() + (cloned[0].limit() - cloned[0].position()));
        return Charset.forName("UTF-8").decode(cloned[1]).toString();
    }

    @NotNull
    private static ByteBuffer[] getTextBuffer(@NotNull ByteBuffer source, boolean terminatorCheck) {
        int originalPosition = source.position();
        int originalLimit = source.limit();
        int inclusiveEnd = source.limit();
        int trimmedEnd = source.limit();
        if (terminatorCheck) {
            while (source.remaining() > 0) {
                if (source.get() == 0x00) { // Check for null terminator
                    inclusiveEnd = source.position();
                    trimmedEnd = source.position() - 1;
                    break;
                }
            }
        }
        source.position(originalPosition);
        source.limit(inclusiveEnd);
        @NotNull ByteBuffer inclusive = source.slice();
        source.limit(trimmedEnd);
        @NotNull ByteBuffer trimmed = source.slice();
        source.limit(originalLimit);
        return new ByteBuffer[]{inclusive, trimmed};
    }

    @NotNull
    public static String getText(@NotNull ByteBuffer source) {
        return getText(source, false);
    }

    @NotNull
    public static String getText(@NotNull ByteBuffer source, boolean terminator) {
        return Charset.forName("UTF-8").decode(getTextBuffer(source, terminator)[1]).toString();
    }

    @NotNull
    public static String readZeroString(@NotNull ByteBuffer buf) {
        @NotNull ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            byte b = buf.get();
            if (b == 0) {
                break;
            }
            baos.write(b);
        }
        //        LOG.info(Arrays.toString(baos.toByteArray()) + "");
        return Charset.forName("UTF-8").decode(ByteBuffer.wrap(baos.toByteArray())).toString();
    }

    @NotNull
    public static String toBinaryString(short n) {
        return toBinaryString(n, 16);
    }

    @NotNull
    private static String toBinaryString(long n, int radix) {
        @NotNull StringBuilder sb = new StringBuilder();
        for (int i = 0; i < radix; i++) {
            sb.append('0');
        }
        for (int bit = 0; bit < radix; bit++) {
            if (((n >> bit) & 1) != 0) {
                sb.setCharAt(radix - 1 - bit, '1');
            }
        }
        return sb.toString();
    }

    @NotNull
    public static String toBinaryString(int n) {
        return toBinaryString(n, 32);
    }

    @NotNull
    public static String toBinaryString(long n) {
        return toBinaryString(n, 64);
    }

    public static int updateChecksumAddSpecial(int value) {
        int checksum = value & 0xFF;
        checksum += (value >> 8) & 0xFF;
        checksum += (value >> 16) & 0xFF;
        checksum += (value >> 24) & 0xFF;
        return checksum;
    }

    public static int updateChecksumAdd(int value) {
        return value;
    }

    @NotNull
    public static String hexDump(@NotNull ByteBuffer buf) {
        int originalPosition = buf.position();
        @NotNull byte[] byt = new byte[buf.limit()];
        buf.get(byt);
        buf.position(originalPosition);
        return Utils.hex(byt);
    }
}
