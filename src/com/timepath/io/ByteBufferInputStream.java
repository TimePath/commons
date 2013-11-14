package com.timepath.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author TimePath
 */
public class ByteBufferInputStream extends InputStream {

    private ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf.asReadOnlyBuffer();
        this.buf.rewind();
    }

    private int markpos = -1;

    private int marklimit;

    public int read() throws IOException {
        if(!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    @Override
    public int available() throws IOException {
        return buf.remaining();
    }

    @Override
    public synchronized void mark(int readlimit) {
        markpos = buf.position();
        marklimit = markpos + readlimit;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void reset() throws IOException {
        if(markpos < 0) {
            throw new IOException("Resetting to invalid mark");
        }
        buf.position(markpos);
    }

    @Override
    public long skip(long n) throws IOException {
        int position = buf.position();
        buf.position(position + (int) n);
        return position - buf.position();
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if(!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, available());
        buf.get(bytes, off, len);
        if(buf.position() > marklimit) {
            markpos = -1;
        }
        return len;
    }

}