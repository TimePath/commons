package com.timepath.plaf;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 */
public enum OS {

    Windows,
    OSX,
    Linux,
    Other;

    private static final Logger LOG = Logger.getLogger(OS.class.getName());

    private final static OS system;

    static {
        String osVer = System.getProperty("os.name").toLowerCase();
        if(osVer.contains("windows")) {
            system = OS.Windows;
        } else if(osVer.contains("mac os x") || osVer.contains("OS X") || osVer.contains("mac")) {
            system = OS.OSX;
        } else if(osVer.contains("Linux") || osVer.contains("nix") || osVer.contains("nux")) {
            system = OS.Linux;
        } else {
            system = OS.Other;
            LOG.log(Level.INFO, "OS string: {0}", osVer);
        }
        LOG.log(Level.INFO, "OS: {0}", system);
    }

    public static OS get() {
        return system;
    }

    public static boolean isWindows() {
        return system == Windows;
    }

    public static boolean isMac() {
        return system == OSX;
    }

    public static boolean isLinux() {
        return system == Linux;
    }

}
