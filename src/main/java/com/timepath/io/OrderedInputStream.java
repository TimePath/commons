package com.timepath.io;

import com.timepath.io.struct.Struct;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderedInputStream extends InputStream implements DataInput {

    private static final Logger LOG = Logger.getLogger(OrderedInputStream.class.getName());
    private final byte[] arr = new byte[8];
    private final ByteBuffer buf = ByteBuffer.wrap(arr);
    @NotNull
    private final DataInputStream in;
    private final int limit;
    private int position;

    public OrderedInputStream(@NotNull InputStream in) throws IOException {
        this(new DataInputStream(in));
    }

    private OrderedInputStream(@NotNull DataInputStream in) throws IOException {
        this.in = in;
        limit = in.available();
    }

    @NotNull
    public ByteOrder order() {
        return buf.order();
    }

    public void order(@NotNull ByteOrder bo) {
        buf.order(bo);
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        position += 1;
        return b;
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    /**
     * Reads a \0 terminated string
     *
     * @return The string without the \0
     * @throws IOException
     */
    @NotNull
    public String readString() throws IOException {
        return readString(0);
    }

    /**
     * Reads a \0 terminated string
     *
     * @param min Minimum number of bytes to read
     * @return The string without the \0
     * @throws IOException
     */
    @NotNull
    public String readString(int min) throws IOException {
        @NotNull ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c;
        loop:
        while (true) {
            switch (c = in.read()) {
                case 0:
                    break loop;
                default:
                    baos.write(c);
                    break;
            }
        }
        int skip = min - (baos.size() + 1);
        if (skip > 0) {
            LOG.log(Level.FINE, "Skipping {0}", skip);
            readFully(new byte[skip]);
            position += skip;
        }
        position += baos.size();
        return new String(baos.toByteArray());
    }

    @Override
    public void readFully(@NotNull byte[] b) throws IOException {
        in.readFully(b);
        position += b.length;
    }

    @Override
    public void readFully(@NotNull byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
        position += len;
    }

    @Override
    public int skipBytes(int n) throws IOException {
        int b = in.skipBytes(n);
        position += b;
        return b;
    }

    @Override
    public boolean readBoolean() throws IOException {
        boolean b = in.readBoolean();
        position += 1;
        return b;
    }

    @Override
    public byte readByte() throws IOException {
        byte b = in.readByte();
        position += 1;
        return b;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b = in.readUnsignedByte();
        position += 1;
        return b;
    }

    @Override
    public short readShort() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getShort();
    }

    @Override
    public char readChar() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getChar();
    }

    @Override
    public int readInt() throws IOException {
        in.readFully(arr, 0, 4);
        position += 4;
        buf.rewind();
        return buf.getInt();
    }

    @Override
    public long readLong() throws IOException {
        in.readFully(arr, 0, 8);
        position += 8;
        buf.rewind();
        return buf.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        in.readFully(arr, 0, 4);
        position += 4;
        buf.rewind();
        return buf.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        in.readFully(arr, 0, 8);
        position += 8;
        buf.rewind();
        return buf.getDouble();
    }

    @Override
    @SuppressWarnings("deprecation")
    public String readLine() throws IOException {
        return in.readLine();
    }

    @NotNull
    @Override
    public String readUTF() throws IOException {
        return in.readUTF();
    }

    @NotNull
    public <S> S readStruct(@NotNull S instance)
            throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException {
        Struct.unpack(instance, this);
        return instance;
    }

    @NotNull
    public <S> S readStruct(@NotNull Class<S> struct) throws IOException, InstantiationException, IllegalAccessException {
        @NotNull S instance = struct.newInstance();
        Struct.unpack(instance, this);
        return instance;
    }

    /**
     * Skips forward to an absolute offset
     *
     * @param offset Somewhere ahead of the current position
     * @throws java.io.IOException
     */
    public void skipTo(int offset) throws IOException {
        skipBytes(offset - position());
    }

    /**
     * @return the position
     * @throws java.io.IOException
     */
    public int position() throws IOException {
        return limit - in.available(); // XXX: FIXME
    }
}
