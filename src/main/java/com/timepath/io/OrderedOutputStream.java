package com.timepath.io;

import com.timepath.io.struct.Struct;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

public class OrderedOutputStream extends OutputStream implements DataOutput {

    private static final Logger LOG = Logger.getLogger(OrderedOutputStream.class.getName());
    private final byte[] arr = new byte[8];
    private final ByteBuffer buf = ByteBuffer.wrap(arr);
    private final DataOutputStream out;
    private int position;

    public OrderedOutputStream(OutputStream out) throws IOException {
        this(new DataOutputStream(out));
    }

    private OrderedOutputStream(DataOutputStream out) throws IOException {
        this.out = out;
    }

    @NotNull
    public ByteOrder order() {
        return buf.order();
    }

    public void order(@NotNull ByteOrder bo) {
        buf.order(bo);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        position++;
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        out.write(b);
        position += b.length;
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        out.write(b);
        position += len;
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public <S> void writeStruct(@NotNull S instance)
            throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException {
        Struct.pack(instance, this);
    }

    public <S> void writeStruct(@NotNull Class<S> struct) throws IOException, InstantiationException, IllegalAccessException {
        S instance = struct.newInstance();
        Struct.pack(instance, this);
    }

    /**
     * @return the position
     * @throws java.io.IOException
     */
    public int position() throws IOException {
        return position;
    }

    private void doWrite() throws IOException {
        int i = buf.position();
        buf.position(0);
        out.write(arr, 0, i);
        position += i;
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        buf.put(v ? (byte) 1 : 0);
        doWrite();
    }

    @Override
    public void writeByte(final int v) throws IOException {
        buf.put((byte) v);
        doWrite();
    }

    @Override
    public void writeShort(final int v) throws IOException {
        buf.putShort((short) v);
        doWrite();
    }

    @Override
    public void writeChar(final int v) throws IOException {
        buf.putChar((char) v);
        doWrite();
    }

    @Override
    public void writeInt(final int v) throws IOException {
        buf.putInt(v);
        doWrite();
    }

    @Override
    public void writeLong(final long v) throws IOException {
        buf.putLong(v);
        doWrite();
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        buf.putFloat(v);
        doWrite();
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        buf.putDouble(v);
        doWrite();
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        throw new UnsupportedOperationException("writeBytes not supported");
    }

    @Override
    public void writeChars(final String s) throws IOException {
        throw new UnsupportedOperationException("writeBytes not supported");
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        throw new UnsupportedOperationException("writeBytes not supported");
    }
}
