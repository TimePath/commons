package com.timepath;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class FileUtils {

    private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
    }

    public static void chmod777(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
    }

    public static String extension(File f) {
        return extension(f.getName());
    }

    /*
     * Get the extension of a file.
     */
    public static String extension(String s) {
        String ext = null;
        int i = s.lastIndexOf('.');
        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String name(String s) {
        return s.substring(s.lastIndexOf('/') + 1);
    }

    public static String checksum(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
        MappedByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        return checksum(buf, algorithm);
    }

    public static String checksum(ByteBuffer buf, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(buf);
        byte[] cksum = md.digest();
        StringBuilder sb = new StringBuilder(cksum.length * 2);
        for (byte aCksum : cksum) {
            sb.append(Integer.toString((aCksum & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }
}
