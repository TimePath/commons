package com.timepath

import org.junit.Assert.assertEquals
import java.util.Comparator
import org.junit.Test as test

/**
 * @author TimePath
 */
public class DiffTest {

    /**
     * Test of diff method, of class Diff.
     */
    test fun testDiff() {
        val expResult = Diff("A" to "A", listOf("C"), listOf("B"), listOf("A"), listOf())
        val caseInsensitive = Comparator<String> { o1, o2 -> o1.compareTo(o2, ignoreCase = true) }
        val original = listOf("A", "B")
        val changed = listOf("A", "C")
        val result = Diff.diff(original, changed, caseInsensitive)
        println("Deleted: " + result.removed + " vs " + expResult.removed)
        println("New: " + result.added + " vs " + expResult.added)
        println("Modified: " + result.modified + " vs " + expResult.modified)
        println("Same: " + result.same + " vs " + expResult.same)
        assertEquals(expResult, result)
    }
}
