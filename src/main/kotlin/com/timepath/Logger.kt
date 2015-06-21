package com.timepath

import java.io.IOException
import java.lang.invoke.MethodHandles
import java.util.logging.Level
import java.util.logging.LogManager

public class Logger(public val logger: java.util.logging.Logger) {
    companion object {
        init {
            javaClass.getResourceAsStream("/logging.properties")?.let {
                try {
                    LogManager.getLogManager().readConfiguration(it)
                } catch(ignore: IOException) {
                }
            }
        }

        suppress("NOTHING_TO_INLINE") inline
        fun invoke() = Logger(java.util.logging.Logger.getLogger(MethodHandles.lookup().lookupClass().getName()))
    }

    public inline fun log(level: Level, msg: () -> String?, thrown: Throwable): Unit = if (logger.isLoggable(level))
        logger.log(level, msg(), thrown)

    public inline fun log(level: Level, msg: () -> String): Unit = if (logger.isLoggable(level)) logger.log(level, msg())
    public inline fun finest(msg: () -> String): Unit = log(Level.FINEST, msg)
    public inline fun finer(msg: () -> String): Unit = log(Level.FINER, msg)
    public inline fun fine(msg: () -> String): Unit = log(Level.FINE, msg)
    public inline fun config(msg: () -> String): Unit = log(Level.CONFIG, msg)
    public inline fun info(msg: () -> String): Unit = log(Level.INFO, msg)
    public inline fun warning(msg: () -> String): Unit = log(Level.WARNING, msg)
    public inline fun severe(msg: () -> String): Unit = log(Level.SEVERE, msg)
}
