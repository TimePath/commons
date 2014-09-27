package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * @author TimePath
 */
public class IOUtils {

    private static final Logger LOG = Logger.getLogger(IOUtils.class.getName());

    private static final ConnectionSettings CONNECTION_SETTINGS_IDENTITY = new ConnectionSettings() {
        @Override
        public void apply(URLConnection u) {

        }
    };

    private IOUtils() {
    }

    /**
     * @param s the URL
     * @return a URLConnection for s
     * @throws java.io.IOException
     */
    public static URLConnection requestConnection(@NotNull String s) throws IOException {
        return requestConnection(s, CONNECTION_SETTINGS_IDENTITY);
    }

    @NotNull
    private static URL toUrl(@NotNull String s) throws IOException {
        try {
            return new URI(s).toURL();
        } catch (URISyntaxException e) {
            throw new IOException("Malformed URI: " + s, e);
        }
    }

    /**
     * @param s the URL
     * @return a URLConnection for s
     * @throws java.io.IOException
     */
    public static URLConnection requestConnection(@NotNull String s, @NotNull ConnectionSettings settings) throws IOException {
        @NotNull URL url = toUrl(s);
        int redirectLimit = 5;
        int retryLimit = 2;
        redirect:
        for (int i = 0; i < redirectLimit + 1; i++) {
            for (int j = 0; j < retryLimit + 1; j++) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(true);
                    connection.setConnectTimeout(10 * 1000); // Initial
                    connection.setReadTimeout(10 * 1000); // During transfer
                    if (connection instanceof HttpURLConnection) { // Includes HttpsURLConnection
                        @NotNull HttpURLConnection conn = ((HttpURLConnection) connection);
                        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
                        conn.setInstanceFollowRedirects(true);
                        settings.apply(conn);
                        int status = conn.getResponseCode();
                        int range = status / 100;
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                                || status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_SEE_OTHER) {
                            url = toUrl(conn.getHeaderField("Location"));
                            conn.disconnect();
                            continue redirect;
                        } else if (range == 4) {
                            j = retryLimit; // Stop
                            throw new FileNotFoundException("HTTP 4xx: " + s);
                        } else if (range != 2 && range != 5) {
                            LOG.log(Level.WARNING, "Unexpected response from {0}: {1}", new Object[]{s, status});
                        }
                    } else {
                        settings.apply(connection);
                    }
                    return connection;
                } catch (IOException e) {
                    if (j == retryLimit) throw e;
                }
            }
        }
        throw new IOException("Too many redirects");
    }

    public static InputStream openStream(@NotNull URLConnection conn) throws IOException {
        String encoding = conn.getHeaderField("Content-Encoding");
        InputStream stream = conn.getInputStream();
        if (encoding != null) {
            LOG.log(Level.FINE, "Decompressing: {0} ({1})", new Object[]{conn.getURL(), encoding});
            switch (encoding.toLowerCase()) {
                case "gzip":
                    return new GZIPInputStream(stream);
                case "deflate":
                    return new InflaterInputStream(stream, new Inflater(true));
            }
        }
        return stream;
    }

    public static InputStream openStream(@NotNull String s) throws IOException {
        return openStream(requestConnection(s));
    }

    /**
     * @param s the URL
     * @return the page text, or null
     */
    @Nullable
    public static String requestPage(@NotNull String s) {
        URLConnection connection;
        try {
            connection = requestConnection(s);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to connect: {0}", s);
            return null;
        }
        try (@NotNull BufferedReader br = new BufferedReader(new InputStreamReader(openStream(connection), StandardCharsets.UTF_8))) {
            @NotNull StringBuilder sb = new StringBuilder(Math.max(connection.getContentLength(), 0));
            for (String line; (line = br.readLine()) != null; ) sb.append('\n').append(line);
            return sb.substring(Math.min(1, sb.length()));
        } catch (IOException ignored) {
            return null;
        }
    }

    public static boolean transfer(@NotNull URL u, @NotNull File file) {
        try (@NotNull InputStream is = new BufferedInputStream(u.openStream())) {
            LOG.log(Level.INFO, "Downloading {0} > {1}", new Object[]{u, file});
            createFile(file);
            @NotNull byte[] buffer = new byte[8192]; // 8K
            try (@NotNull FileOutputStream fos = new FileOutputStream(file)) {
                for (int read; (read = is.read(buffer)) > -1; ) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
            }
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean createFile(@NotNull File file) throws IOException {
        return file.mkdirs() && file.delete() && file.createNewFile();
    }

    public static interface ConnectionSettings {
        void apply(URLConnection u);
    }
}
