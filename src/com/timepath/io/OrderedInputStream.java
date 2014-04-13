package com.timepath.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

public class OrderedInputStream extends InputStream implements DataInput {

    private static final Logger LOG = Logger.getLogger(OrderedInputStream.class.getName());

    private final byte[] arr = new byte[8];

    private final ByteBuffer buf = ByteBuffer.wrap(arr);

    private final DataInputStream in;
    
    private final int limit;
    
    private int position;

    public OrderedInputStream(final InputStream in) throws IOException {
        this.in = new DataInputStream(in);
        limit = in.available();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public ByteOrder order() {
        return buf.order();
    }

    public void order(ByteOrder bo) {
        buf.order(bo);
    }

    /**
     * @return the position
     * @throws java.io.IOException
     */
    public int position() throws IOException {
        return limit - in.available(); // XXX: FIXME
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        position += 1;
        return b;
    }


    public boolean readBoolean() throws IOException {
        boolean b = in.readBoolean();
        position += 1;
        return b;
    }

    public byte readByte() throws IOException {
        byte b = in.readByte();
        position += 1;
        return b;
    }

    public char readChar() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getChar();
    }

    public double readDouble() throws IOException {
        in.readFully(arr, 0, 8);
        position += 8;
        buf.rewind();
        return buf.getDouble();
    }

    public float readFloat() throws IOException {
        in.readFully(arr, 0, 4);
        position += 4;
        buf.rewind();
        return buf.getFloat();
    }

    public void readFully(byte[] b) throws IOException {
        in.readFully(b);
        position += b.length;
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
        position += len;
    }

    public int readInt() throws IOException {
        in.readFully(arr, 0, 4);
        position += 4;
        buf.rewind();
        return buf.getInt();
    }

    @SuppressWarnings("deprecation")
    public String readLine() throws IOException {
        return in.readLine();
    }

    public long readLong() throws IOException {
        in.readFully(arr, 0, 8);
        position += 8;
        buf.rewind();
        return buf.getLong();
    }

    public short readShort() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getShort();
    }

    /**
     * Reads a \0 terminated string
     * <p/>
     * @return The string without the \0
     * <p/>
     * @throws IOException
     */
    public String readString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c;
        loop:
            for(;;) {
                switch(c = in.read()) {
                    case 0:
                        break loop;
                    default:
                        baos.write(c);
                        break;
                }
            }
        position += baos.size();
        return new String(baos.toByteArray());
    }

    public String readUTF() throws IOException {
        return in.readUTF();
    }

    public int readUnsignedByte() throws IOException {
        int b = in.readUnsignedByte();
        position += 1;
        return b;
    }

    public int readUnsignedShort() throws IOException {
        in.readFully(arr, 0, 2);
        position += 2;
        buf.rewind();
        return buf.getShort();
    }

    public int skipBytes(int n) throws IOException {
        int b = in.skipBytes(n);
        position += b;
        return b;
    }

}
