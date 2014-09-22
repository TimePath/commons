package com.timepath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @param <X>
 * @author TimePath
 */
public class Diff<X> {

    private static final Logger LOG = Logger.getLogger(Diff.class.getName());
    @SuppressWarnings("rawtypes")
    private static final Comparator EMPTY_COMPARATOR = new Comparator() {
        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        @Override
        public int compare(Object o1, Object o2) {
            return 0;
        }
    };
    public List<X> added, removed, same;
    public X in, out;
    public List<Pair<X, X>> modified;

    public Diff() {
    }

    @SuppressWarnings("unchecked")
    public static <X> Diff<X> diff(X original, X changed, Comparator<X> similar, Comparator<X> exact) {
        return diff(Arrays.asList(original), Arrays.asList(changed), similar, exact);
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
    @SuppressWarnings("unchecked") // EMPTY_COMPARATOR
    public static <X> Diff<X> diff(List<X> original, List<X> changed, Comparator<X> similar, Comparator<X> exact) {
        Diff<X> d = new Diff<>();
        List<X> added = new LinkedList<>(changed);
        List<X> removed = new LinkedList<>(original);
        List<Pair<X, X>> modified = new LinkedList<>();
        List<X> same = new LinkedList<>();
        if (exact == null) {
            exact = EMPTY_COMPARATOR;
        }
        for (X a : original) {
            for (X b : changed) {
                if (similar.compare(a, b) == 0) {
                    added.remove(b);
                    removed.remove(a);
                    if (exact.compare(a, b) != 0) {
                        modified.add(new Pair<>(a, b));
                    } else {
                        same.add(a);
                    }
                }
            }
        }
        d.added = added;
        d.removed = removed;
        d.modified = modified;
        d.same = same;
        return d;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + ((added != null) ? added.hashCode() : 0);
        hash = 53 * hash + ((removed != null) ? removed.hashCode() : 0);
        hash = 53 * hash + ((same != null) ? same.hashCode() : 0);
        hash = 53 * hash + ((modified != null) ? modified.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Diff<?> other = (Diff<?>) obj;
        if ((added != other.added) && ((added == null) || !added.equals(other.added))) {
            return false;
        }
        if ((removed != other.removed) && ((removed == null) || !removed.equals(other.removed))) {
            return false;
        }
        if ((same != other.same) && ((same == null) || !same.equals(other.same))) {
            return false;
        }
        // Technically valid at this point, no need to check modified
        return true;
    }
}
