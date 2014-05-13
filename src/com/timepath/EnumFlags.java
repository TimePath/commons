package com.timepath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public final class EnumFlags {

    private static final Logger LOG = Logger.getLogger(EnumFlags.class.getName());

    private EnumFlags() {
    }

    @SuppressWarnings("unchecked")
    public static <C extends Enum<C> & EnumFlag> EnumSet<C> decode(int encoded, Class<C> enumClass) {
        C[] map = enumClass.getEnumConstants();
        // Mixed bits at the top, single bits at the bottom, in order of 1s
        Arrays.sort(map, new Comparator<C>() {
            @Override
            public int compare(C e1, C e2) {
                int i1 = e1.getId();
                int i2 = e2.getId();
                int diff = Integer.bitCount(i2) - Integer.bitCount(i1);
                return ( diff == 0 ) ? ( i2 - i1 ) : diff; // If same amount of bits, higher value first
            }
        });
        for(C entry : map) {
            if(entry.getId() == encoded) {
                LOG.log(Level.FINER, "{0} = {1}", new Object[] { entry, encoded });
                return EnumSet.of(entry);
            }
        }
        EnumSet<C> ret = EnumSet.noneOf(enumClass);
        for(C entry : map) {
            if(entry.getId() == 0) {
                continue;
            }
            if(( encoded & entry.getId() ) == entry.getId()) {
                ret.add(entry);
            }
        }
        LOG.log(Level.FINER, "{0} = {1}", new Object[] { ret, encoded });
        return ret;
    }

    public static <C extends Enum<C>> int encode(Iterable<C> set) {
        int ret = 0;
        for(C val : set) {
            ret |= 1 << val.ordinal();
        }
        return ret;
    }
}
