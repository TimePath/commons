package com.timepath.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author TimePath
 */
public class AggregateOutputStream extends OutputStream {

    public AggregateOutputStream() {
    }

    private final List<OutputStream> out = new LinkedList<OutputStream>();

    public void register(OutputStream outputStream) {
        synchronized(out) {
            out.add(outputStream);
        }
    }

    public void deregister(OutputStream outputStream) {
        synchronized(out) {
            out.remove(outputStream);
        }
    }

    @Override
    public void write(int b) throws IOException {
        List<OutputStream> dereg = new LinkedList<OutputStream>();
        synchronized(out) {
            for(OutputStream os : out) {
                try {
                    os.write(b);
                } catch(IOException e) {
                    dereg.add(os);
                }
            }
            out.removeAll(dereg);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        List<OutputStream> streams = new LinkedList<OutputStream>();
        List<OutputStream> dereg = new LinkedList<OutputStream>();
        synchronized(out) {
            streams.addAll(out);
        }
        for(OutputStream os : streams) {
            try {
                os.write(b, off, len);
            } catch(IOException e) {
                dereg.add(os);
            }
        }
        if(!dereg.isEmpty()) {
            synchronized(out) {
                out.removeAll(dereg);
            }
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

}
