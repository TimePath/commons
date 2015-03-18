package com.timepath


import java.util.Arrays
import java.util.Comparator
import java.util.EnumSet
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.platform.platformStatic

/**
 * @author TimePath
 */
public object EnumFlags {

    private val LOG = Logger.getLogger(javaClass<EnumFlags>().getName())

    SuppressWarnings("unchecked")
    public platformStatic fun <C : Enum<C>> decode(encoded: Int, enumClass: Class<C>): EnumSet<C> where C : EnumFlag {
        val map = enumClass.getEnumConstants()
        // Mixed bits at the top, single bits at the bottom, in order of 1s
        Arrays.sort<C>(map, object : Comparator<C> {
            override fun compare(e1: C, e2: C): Int {
                val i1 = e1.getId()
                val i2 = e2.getId()
                val diff = Integer.bitCount(i2) - Integer.bitCount(i1)
                return if ((diff == 0)) (i2 - i1) else diff // If same amount of bits, higher value first
            }
        })
        for (entry in map) {
            if (entry.getId() == encoded) {
                LOG.log(Level.FINER, "{0} = {1}", array<Any>(entry, encoded))
                return EnumSet.of<C>(entry)
            }
        }
        val ret = EnumSet.noneOf<C>(enumClass)
        for (entry in map) {
            if (entry.getId() == 0) {
                continue
            }
            if ((encoded and entry.getId()) == entry.getId()) {
                ret.add(entry)
            }
        }
        LOG.log(Level.FINER, "{0} = {1}", array(ret, encoded))
        return ret
    }

    public platformStatic fun <C : Enum<C>> encode(set: Iterable<C>): Int {
        var ret = 0
        for (`val` in set) {
            ret = ret or (1 shl `val`.ordinal())
        }
        return ret
    }
}
