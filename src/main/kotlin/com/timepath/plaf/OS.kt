package com.timepath.plaf


import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author TimePath
 */
public enum class OS {
    Windows,
    OSX,
    Linux,
    Other;

    companion object {

        private val LOG = Logger.getLogger(javaClass<OS>().getName())

        public fun get(): OS {
            val osVer = System.getProperty("os.name").toLowerCase()
            val system = when {
                "windows" in osVer -> Windows
                "mac os x" in osVer, "OS X" in osVer, "mac" in osVer -> OSX
                "Linux" in osVer, "nix" in osVer, "nux" in osVer -> Linux
                else -> {
                    LOG.log(Level.WARNING, "Unknown OS string: {0}", osVer)
                    Other
                }
            }
            LOG.log(Level.INFO, "OS: {0}", system)
            return system
        }

        public fun isWindows(): Boolean = get() == Windows

        public fun isMac(): Boolean = get() == OSX

        public fun isLinux(): Boolean = get() == Linux
    }

}
