package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Attributes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class JARUtils {

    private static final Logger LOG = Logger.getLogger(JARUtils.class.getName());

    public static long version(@NotNull Class<?> clazz) {
        String impl = clazz.getPackage().getImplementationVersion();
        if (impl != null) {
            try {
                return Long.parseLong(impl);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    @Nullable
    public static String getMainClassName(URL url) throws IOException {
        @NotNull URL u = new URL("jar", "", url + "!/");
        @NotNull JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        return (attr != null) ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
    }

    @Nullable
    private static String selfCheck(@NotNull Class<?> c) {
        @Nullable String md5 = null;
        @NotNull String runPath = Utils.currentFile(c).getName();
        if (runPath.endsWith(".jar")) {
            try {
                md5 = Utils.takeMD5(Utils.loadFile(new File(runPath)));
            } catch (@NotNull IOException | NoSuchAlgorithmException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return md5;
    }
}
