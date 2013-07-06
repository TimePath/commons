package com.timepath;

import java.util.logging.Logger;

/**
 *
 * @author timepath
 */
public class StringUtils {

    public static String capitalize(String str) {
        if(str == null) {
            return null;
        }
        if(str.length() == 0) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + str.length() > 1 ? str.substring(1).toLowerCase() : "";
    }

    private StringUtils() {
    }

    private static final Logger LOG = Logger.getLogger(StringUtils.class.getName());

    public static String fromDoubleArray(Object[][] debug, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        for(int l = 0; l < debug.length; l++) {
            for(int x = 0; x < debug[l].length; x++) {
                sb.append(debug[l][x]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
