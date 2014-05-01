package com.timepath.io.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author TimePath
 */
public interface Savable {

    void readExternal(InputStream in);

    void readExternal(ByteBuffer buf);

    void writeExternal(OutputStream out);

}
