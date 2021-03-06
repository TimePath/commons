package com.timepath.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author TimePath
 */
public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {
        buf.put((byte) b);
    }

    @Override
    public void write(@NotNull byte[] bytes, int off, int len) throws IOException {
        buf.put(bytes, off, len);
    }
}
