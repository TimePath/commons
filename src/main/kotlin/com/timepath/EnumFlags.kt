package com.timepath


import java.util.Arrays
import java.util.Comparator
import java.util.EnumSet

/** Implement the [EnumFlags.Flag] interface to use these utilities */
public object EnumFlags {

    public interface Flag<E : Enum<E>> {
        public val id: Int get() = 1 shl (this as Enum<*>).ordinal()
    }

    public fun decode<E : Flag<E>>(encoded: Int, enumClass: Class<E>): EnumSet<E> where E : Enum<E> {
        val map = enumClass.getEnumConstants()
        // Mixed bits at the top, single bits at the bottom, in order of 1s
        Arrays.sort<E>(map, Comparator { e1, e2 ->
            val i1 = e1.id
            val i2 = e2.id
            val diff = Integer.bitCount(i2) - Integer.bitCount(i1)
            when (diff) {
                0 -> i2 - i1 // If same amount of bits, higher value first
                else -> diff
            }
        })
        // Try an exact match
        for (entry in map) {
            if (entry.id == encoded) return EnumSet.of(entry)
        }
        // Aggregate
        val ret = EnumSet.noneOf(enumClass)
        for (entry in map) {
            if (entry.id == 0) continue
            if ((encoded and entry.id) == entry.id) ret.add(entry)
        }
        return ret
    }

    public fun encode<E : Flag<E>>(set: Iterable<E>): Int {
        var ret = 0
        for (flag in set) {
            ret = ret or flag.id
        }
        return ret
    }
}
