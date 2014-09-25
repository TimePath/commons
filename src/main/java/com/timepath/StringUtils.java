package com.timepath;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class StringUtils {

    private static final Logger LOG = Logger.getLogger(StringUtils.class.getName());

    private StringUtils() {
    }

    public static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + ((str.length() > 1) ? str.substring(1).toLowerCase() : "");
    }

    public static String join(CharSequence delim, Object... args) {
        return join(delim, JoinAcceptor.ALL, args);
    }

    @SafeVarargs
    public static <T> String join(CharSequence delim, JoinAcceptor<T> a, T... args) {
        delim = String.valueOf(delim);
        StringBuilder sb = new StringBuilder();
        for (T o : args) {
            if (!a.accept(o)) continue;
            sb.append(delim).append(String.valueOf(o));
        }
        return sb.substring(Math.min(delim.length(), sb.length()));
    }

    public static String fromDoubleArray(Object[][] debug, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append('\n');
        for (Object[] debug1 : debug) {
            for (Object item : debug1) {
                sb.append(item);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static List<String> argParse(String cmd) {
        if (cmd == null) return null;
        return Arrays.asList(cmd.split(" "));
    }

    public static interface JoinAcceptor<T> {
        public static JoinAcceptor ALL = new JoinAcceptor<Object>() {
            @Override
            public boolean accept(Object o) {
                return true;
            }
        };

        boolean accept(T t);
    }
}
