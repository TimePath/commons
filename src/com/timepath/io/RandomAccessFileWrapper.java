package com.timepath.io;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * @author TimePath
 */
public class RandomAccessFileWrapper {

    private final RandomAccessFile raf;

    public RandomAccessFileWrapper(File file, String mode) throws FileNotFoundException {
        raf = new RandomAccessFile(file, mode);
    }

    public RandomAccessFileWrapper(String name, String mode) throws FileNotFoundException {
        raf = new RandomAccessFile(name, mode);
    }

    public RandomAccessFileWrapper(RandomAccessFile raf) {
        this.raf = raf;
    }

    public void close() throws IOException {
        raf.close();
    }

    public FileChannel getChannel() {
        return raf.getChannel();
    }

    public FileDescriptor getFD() throws IOException {
        return raf.getFD();
    }

    public long getFilePointer() throws IOException {
        return raf.getFilePointer();
    }

    public long length() throws IOException {
        return raf.length();
    }

    public void setLength(long newLength) throws IOException {
        raf.setLength(newLength);
    }

    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public void skipBytes(long n) throws IOException {
        raf.seek(raf.getFilePointer() + n);
    }

    public String readLine() throws IOException {
        return raf.readLine();
    }

    public String readUTF() throws IOException {
        return raf.readUTF();
    }

    public void writeUTF(String str) throws IOException {
        raf.writeUTF(str);
    }

    public int read() throws IOException {
        return raf.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }

    public void write(int b) throws IOException {
        raf.write(b);
    }

    public void write(byte[] b) throws IOException {
        raf.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
    }

    public boolean readBoolean() throws IOException {
        return raf.readBoolean();
    }

    public void writeBoolean(boolean v) throws IOException {
        raf.writeBoolean(v);
    }

    public byte[] readBytes(int num) throws IOException {
        byte[] arr = new byte[num];
        read(arr);
        return arr;
    }

    public int read(byte... b) throws IOException {
        return raf.read(b);
    }

    public void writeBytes(String s) throws IOException {
        raf.writeBytes(s);
    }

    public char readChar() throws IOException {
        return raf.readChar();
    }

    public char readLEChar() throws IOException {
        return (char) ( readUnsignedByte() + ( readUnsignedByte() << 8 ) );
    }

    int readUnsignedByte() throws IOException {
        return raf.readUnsignedByte();
    }

    public void writeChar(int v) throws IOException {
        raf.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
        raf.writeChars(s);
    }

    public void writeLEChars(CharSequence s) throws IOException {
        for(int i = 0; i < s.length(); i++) {
            writeLEChar(s.charAt(i));
        }
    }

    void writeLEChar(int v) throws IOException {
        writeByte(0xFF & v);
        writeByte(0xFF & ( v >> 8 ));
    }

    void writeByte(int v) throws IOException {
        raf.writeByte(v);
    }

    public short readShort() throws IOException {
        return raf.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return raf.readUnsignedShort();
    }

    public short readLEShort() throws IOException {
        return (short) ( ( readByte() & 0xFF ) + ( readByte() << 8 ) );
    }

    byte readByte() throws IOException {
        return raf.readByte();
    }

    public int readULEShort() throws IOException {
        return readUnsignedByte() + ( readUnsignedByte() << 8 );
    }

    public void writeShort(int v) throws IOException {
        raf.writeShort(v);
    }

    public void writeLEShort(short value) throws IOException {
        writeByte(value);
        writeByte(value >> 8);
    }

    public void writeULEShort(short value) throws IOException {
        writeByte(value & 0xFF);
        writeByte(( value >> 8 ) & 0xFF);
    }

    public int readInt() throws IOException {
        return raf.readInt();
    }

    public int readLEInt() throws IOException {
        return readUnsignedByte() + ( readUnsignedByte() << 8 ) + ( readUnsignedByte() << 16 ) +
               ( readUnsignedByte() << 24 );
    }

    public int readULEInt() throws IOException {
        return readUnsignedByte() + ( readUnsignedByte() << 8 ) + ( readUnsignedByte() << 16 ) +
               ( readUnsignedByte() << 24 );
    }

    public void writeInt(int v) throws IOException {
        raf.writeInt(v);
    }

    public float readFloat() throws IOException {
        return raf.readFloat();
    }

    public float readLEFloat() throws IOException {
        int intBits = readUnsignedByte() + ( readUnsignedByte() << 8 ) + ( readUnsignedByte() << 16 ) +
                      ( readUnsignedByte() << 24 );
        return Float.intBitsToFloat(intBits);
    }

    public void writeFloat(int v) throws IOException {
        raf.writeFloat(v);
    }

    public long readLong() throws IOException {
        return raf.readLong();
    }

    public int readULELong() throws IOException {
        return readUnsignedByte() + ( readUnsignedByte() << 8 ) + ( readUnsignedByte() << 16 ) +
               ( readUnsignedByte() << 24 );
    }

    public void writeLong(int v) throws IOException {
        raf.writeLong(v);
    }

    public void writeULong(int value) throws IOException {
        writeLEInt(value);
    }

    void writeLEInt(int value) throws IOException {
        writeByte(value & 0xFF);
        writeByte(( value >> 8 ) & 0xFF);
        writeByte(( value >> 16 ) & 0xFF);
        writeByte(( value >> 24 ) & 0xFF);
    }

    public double readDouble() throws IOException {
        return raf.readDouble();
    }

    public void writeDouble(int v) throws IOException {
        raf.writeDouble(v);
    }
}
