package com.timepath.util.concurrent


import com.timepath.with
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.concurrent.daemon

public class DaemonThreadFactory : ThreadFactory {

    private val threadFactory = Executors.defaultThreadFactory()

    override fun newThread(r: Runnable) = threadFactory.newThread(r).with { daemon = true }
}
