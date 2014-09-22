package com.timepath;

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
}
