package com.timepath

import java.util.Comparator
import org.junit.Assert.assertEquals
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
        val caseInsensitive = Comparator {(o1: String, o2: String) -> o1.compareToIgnoreCase(o2) }
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
