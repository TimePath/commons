package com.timepath.util.concurrent


import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

public class DaemonThreadFactory : ThreadFactory {

    private val threadFactory = Executors.defaultThreadFactory()

    override fun newThread(r: Runnable) = threadFactory.newThread(r).apply { isDaemon = true }
}
