package com.timepath

import java.math.BigInteger
import java.util.concurrent.ExecutorService

@Deprecated("in std", ReplaceWith("this.apply(f)"))
public inline fun <T : Any, R> T.with(f: T.() -> R): T = let { f(); it }

public inline fun ExecutorService.use<T>(body: ExecutorService.() -> T): T = body().apply { shutdown() }

public fun Byte.toUnsigned(): Int = this.toInt() and 0xff
public fun Short.toUnsigned(): Int = this.toInt() and 0xffff
public fun Int.toUnsigned(): Long = this.toLong() and 0xffffffffL
public fun Long.toUnsigned(): BigInteger {
    val quot = (this ushr 1) / 5
    val rem = this - quot * 10
    return BigInteger(quot.toString() + rem)
}

public fun log(n: Int, i: Int): Int = (Math.log(i.toDouble()) / Math.log(n.toDouble())).toInt()
