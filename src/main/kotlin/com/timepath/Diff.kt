package com.timepath


import java.util.*

/**
 * @param <X>
 * @author TimePath
 */
public data class Diff<X>(
        public val comparison: kotlin.Pair<X, X>,
        public val added: List<X>,
        public val removed: List<X>,
        public val same: List<X>,
        public val modified: List<Pair<X, X>>
) {

    companion object {

        private val LOG = Logger()

        public @JvmStatic fun <X> diff(original: X, changed: X, similar: Comparator<X>, exact: Comparator<X>): Diff<X> {
            return diff(Arrays.asList<X>(original), Arrays.asList<X>(changed), similar, exact)
        }

        /**
         * @param <X>      Type of object in list
         * @param original The list of original objects
         * @param changed  The list of modified objects
         * @param similar  Comparator to roughly compare objects
         * @param exact    Comparator for exact checking. May be null if <tt>similar</tt> performs exact checking
         * @return Three lists: Objects now in changed, Objects only in changed, Objects modified in changed (requires exact
         * Comparator)
         */
        public @JvmStatic fun <X> diff(original: List<X>, changed: List<X>, similar: Comparator<X>, exact: Comparator<X>? = null): Diff<X> {
            @Suppress("NAME_SHADOWING")
            val exact = exact ?: Comparator { o1, o2 -> 0 }
            val added = LinkedList(changed)
            val removed = LinkedList(original)
            val modified = LinkedList<Pair<X, X>>()
            val same = LinkedList<X>()
            for (a in original) {
                for (b in changed) {
                    if (similar.compare(a, b) == 0) {
                        added.remove(b)
                        removed.remove(a)
                        if (exact.compare(a, b) != 0) {
                            modified.add(Pair(a, b))
                        } else {
                            same.add(a)
                        }
                    }
                }
            }
            return Diff(
                    comparison = original.first() to changed.first(),
                    added = added,
                    removed = removed,
                    modified = modified,
                    same = same
            )
        }
    }
}
