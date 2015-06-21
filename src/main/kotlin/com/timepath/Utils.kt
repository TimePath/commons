package com.timepath

import java.util.concurrent.ExecutorService

public inline fun <T : Any, R> T.with(f: T.() -> R): T = let { f(); it }

public inline fun ExecutorService.use<T>(body: ExecutorService.() -> T): T = body().with { shutdown() }
