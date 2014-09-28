package com.timepath.util.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author TimePath
 */
public class DaemonThreadFactory implements ThreadFactory {

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = threadFactory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}
