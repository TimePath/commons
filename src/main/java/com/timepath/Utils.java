package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class Utils {

    public static final Comparator<File> ALPHA_COMPARATOR = new Comparator<File>() {
        /**
         * Alphabetically sorts directories before files ignoring case.
         */
        @Override
        public int compare(@NotNull File a, @NotNull File b) {
            if (a.isDirectory() && !b.isDirectory()) {
                return -1;
            } else {
                return (!a.isDirectory() && b.isDirectory()) ? 1 : a.getName().compareToIgnoreCase(b.getName());
            }
        }
    };
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());
    @Nullable
    private static final HyperlinkListener linkListener = new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(@NotNull HyperlinkEvent he) {
            if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        @Nullable URI u = null;
                        URL l = he.getURL();
                        if (l == null) {
                            u = new URI(he.getDescription());
                        } else if (u == null) {
                            u = l.toURI();
                        }
                        Desktop.getDesktop().browse(u);
                    } catch (@NotNull URISyntaxException | IOException e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                }
            }
        }
    };

    private Utils() {
    }

    @Nullable
    public static HyperlinkListener getLinkListener() {
        return linkListener;
    }

    public static void setFinalStatic(@NotNull Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @NotNull
    public static String hex(@NotNull byte... a) {
        @NotNull StringBuilder sb = new StringBuilder();
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff)).append(' ');
        }
        return sb.toString().toUpperCase().trim();
    }

    @NotNull
    public static String normalisePath(@NotNull String str) {
        LOG.log(Level.INFO, "Normalising {0}", str);
        while (str.contains(File.separator + File.separator)) {
            str = str.replaceAll(File.separator + File.separator, File.separator);
        }
        if (!str.endsWith(File.separator)) {
            str += File.separator;
        }
        return str;
    }

    @NotNull
    public static String workingDirectory(@NotNull Class<?> c) {
        return currentFile(c).getParentFile().getAbsolutePath();
    }

    @NotNull
    public static File currentFile(@NotNull Class<?> clazz) {
        String encoded = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            return new File(URLDecoder.decode(encoded, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, "Broken JVM implementation", ex);
        }
        @NotNull String ans = System.getProperty("user.dir") + File.separator;
        String cmd = System.getProperty("sun.java.command");
        int idx = cmd.lastIndexOf(File.separator);
        return new File(ans + ((idx < 0) ? "" : cmd.substring(0, idx + 1)));
    }

    public static boolean isMD5(@NotNull String str) {
        return str.matches("[a-fA-F0-9]{32}");
    }

    @NotNull
    public static String takeMD5(byte... bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        byte[] b = md.digest();
        @NotNull String md5 = "";
        for (byte aB : b) {
            md5 += Integer.toString((aB & 0xFF) + 256, 16).substring(1);
        }
        return md5;
    }

    @NotNull
    public static byte[] loadFile(@NotNull File f) throws IOException {
        @NotNull InputStream fis = new FileInputStream(f);
        @NotNull byte[] buff = new byte[fis.available()];
        int size = 0;
        while (true) {
            int numRead = fis.read(buff);
            if (numRead == -1) {
                break;
            } else {
                size += numRead;
            }
        }
        fis.close();
        @NotNull byte[] ret = new byte[size];
        System.arraycopy(buff, 0, ret, 0, ret.length);
        return ret;
    }

    @Nullable
    public static String pprint(@NotNull Map<String, ?> map) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("root");
            document.appendChild(root);
            for (Element e : pprint(map, document)) {
                root.appendChild(e);
            }
            return XMLUtils.pprint(new DOMSource(document), 2);
        } catch (ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @NotNull
    private static java.util.List<Element> pprint(@NotNull Map<?, ?> map, @NotNull Document document) {
        @NotNull java.util.List<Element> elems = new LinkedList<>();
        for (@NotNull Map.Entry<?, ?> entry : map.entrySet()) {
            Element e = document.createElement("entry");
            e.setAttribute("key", String.valueOf(entry.getKey()));
            if (entry.getValue() instanceof Map) {
                for (Element child : pprint((Map<?, ?>) entry.getValue(), document)) {
                    e.appendChild(child);
                }
            } else {
                e.setAttribute("value", String.valueOf(entry.getValue()));
            }
            elems.add(e);
        }
        return elems;
    }
}
