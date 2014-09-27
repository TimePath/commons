package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * @author TimePath
 */
public class DiffTest {

    private static final Logger LOG = Logger.getLogger(DiffTest.class.getName());

    /**
     * Test of diff method, of class Diff.
     */
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testDiff() {
        System.out.println("diff");
        @NotNull Diff expResult = new Diff();
        expResult.removed = Arrays.asList("B");
        expResult.added = Arrays.asList("C");
        expResult.modified = Arrays.asList();
        expResult.same = Arrays.asList("A");
        @NotNull Comparator<String> caseInsensitive = new Comparator<String>() {
            public int compare(@NotNull String o1, @NotNull String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        };
        @NotNull List<String> original = Arrays.asList("A", "B");
        @NotNull List<String> changed = Arrays.asList("A", "C");
        @NotNull Diff<String> result = Diff.diff(original, changed, caseInsensitive, null);
        System.out.println("Deleted: " + result.removed + " vs " + expResult.removed);
        System.out.println("New: " + result.added + " vs " + expResult.added);
        System.out.println("Modified: " + result.modified + " vs " + expResult.modified);
        System.out.println("Same: " + result.same + " vs " + expResult.same);
        assertEquals(expResult, result);
    }
}
