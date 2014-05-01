package com.timepath;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 * @param <X>
 */
public class Diff<X> {

    public List<X> added, removed, same;

    public X in, out;

    public List<Pair<X, X>> modified;

    private static final Logger LOG = Logger.getLogger(Diff.class.getName());

    @SuppressWarnings("rawtypes")
    private static final Comparator EMPTY_COMPARATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
            return 0;
        }

    };

    /**
     *
     * @param <X>      Type of object in list
     * @param original The list of original objects
     * @param changed  The list of modified objects
     * @param similar  Comparator to roughly compare objects
     * @param exact    Comparator for exact checking. May be null if <tt>similar</tt> performs exact checking
     * <p>
     * @return Three lists: Objects now in changed, Objects only in changed, Objects modified in changed (requires exact Comparator)
     */
    @SuppressWarnings("unchecked") // EMPTY_COMPARATOR
    public static <X> Diff<X> diff(List<X> original, List<X> changed,
                                   Comparator<X> similar, Comparator<X> exact) {
        Diff<X> d = new Diff<X>();

        List<X> added = new LinkedList<X>(changed);
        List<X> removed = new LinkedList<X>(original);
        List<Pair<X, X>> modified = new LinkedList<Pair<X, X>>();
        List<X> same = new LinkedList<X>();
        if(exact == null) {
            exact = EMPTY_COMPARATOR;
        }
        for(X a : original) {
            for(X b : changed) {
                if(similar.compare(a, b) == 0) {
                    added.remove(b);
                    removed.remove(a);
                    if(exact.compare(a, b) != 0) {
                        modified.add(new Pair<X, X>(a, b));
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
    
    @SuppressWarnings("unchecked")
    public static <X> Diff<X> diff(X original, X changed, Comparator<X> similar, Comparator<X> exact) {
        return diff(Arrays.asList(original), Arrays.asList(changed), similar, exact);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.added != null ? this.added.hashCode() : 0);
        hash = 53 * hash + (this.removed != null ? this.removed.hashCode() : 0);
        hash = 53 * hash + (this.same != null ? this.same.hashCode() : 0);
        hash = 53 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Diff<?> other = (Diff<?>) obj;
        if(this.added != other.added && (this.added == null || !this.added.equals(other.added))) {
            return false;
        }
        if(this.removed != other.removed && (this.removed == null || !this.removed.equals(
                                             other.removed))) {
            return false;
        }
        if(this.same != other.same && (this.same == null || !this.same.equals(other.same))) {
            return false;
        }
        // Technically valid at this point
//        if(this.modified != other.modified && (this.modified == null || !this.modified.equals(other.modified))) {
//            return false;
//        }
        return true;
    }

}
