package com.timepath.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author TimePath
 */
public class AggregateOutputStream extends OutputStream {

    private final Collection<OutputStream> out = new LinkedList<>();

    public AggregateOutputStream() {
    }

    public void register(OutputStream outputStream) {
        synchronized (out) {
            out.add(outputStream);
        }
    }

    public void deregister(OutputStream outputStream) {
        synchronized (out) {
            out.remove(outputStream);
        }
    }

    @Override
    public void write(int b) throws IOException {
        @NotNull Collection<OutputStream> dereg = new LinkedList<>();
        synchronized (out) {
            for (@NotNull OutputStream os : out) {
                try {
                    os.write(b);
                } catch (IOException ignored) {
                    dereg.add(os);
                }
            }
            out.removeAll(dereg);
        }
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        @NotNull Collection<OutputStream> streams = new LinkedList<>();
        @NotNull Collection<OutputStream> dereg = new LinkedList<>();
        synchronized (out) {
            streams.addAll(out);
        }
        for (@NotNull OutputStream os : streams) {
            try {
                os.write(b, off, len);
            } catch (IOException ignored) {
                dereg.add(os);
            }
        }
        if (!dereg.isEmpty()) {
            synchronized (out) {
                out.removeAll(dereg);
            }
        }
    }
}
