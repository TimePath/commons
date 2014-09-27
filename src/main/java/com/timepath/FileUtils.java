package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author TimePath
 */
public class FileUtils {

    private FileUtils() {
    }

    public static void chmod777(@NotNull File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
    }

    @Nullable
    public static String extension(@NotNull File f) {
        return extension(f.getName());
    }

    /*
     * Get the extension of a file.
     */
    @Nullable
    public static String extension(@NotNull String s) {
        @Nullable String ext = null;
        int i = s.lastIndexOf('.');
        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @NotNull
    public static String name(@NotNull String s) {
        return s.substring(s.lastIndexOf('/') + 1);
    }

    @NotNull
    public static String checksum(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
        MappedByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        return checksum(buf, algorithm);
    }

    @NotNull
    public static String checksum(ByteBuffer buf, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(buf);
        byte[] cksum = md.digest();
        @NotNull StringBuilder sb = new StringBuilder(cksum.length * 2);
        for (byte aCksum : cksum) {
            sb.append(Integer.toString((aCksum & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }

    @NotNull
    public static String name(@NotNull URL u) {
        return name(u.getFile());
    }
}
