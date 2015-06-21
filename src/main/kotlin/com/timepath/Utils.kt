package com.timepath

import java.math.BigInteger
import java.util.concurrent.ExecutorService

public inline fun <T : Any, R> T.with(f: T.() -> R): T = let { f(); it }

public inline fun ExecutorService.use<T>(body: ExecutorService.() -> T): T = body().with { shutdown() }

public fun Byte.toUnsigned(): Int = java.lang.Byte.toUnsignedInt(this)
public fun Short.toUnsigned(): Int = java.lang.Short.toUnsignedInt(this)
public fun Int.toUnsigned(): Long = java.lang.Integer.toUnsignedLong(this)
public fun Long.toUnsigned(): BigInteger = java.lang.Long.toUnsignedString(this) let { BigInteger(it) }

public fun log(n: Int, i: Int): Int = (Math.log(i.toDouble()) / Math.log(n.toDouble())).toInt()
