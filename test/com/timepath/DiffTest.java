package com.timepath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author TimePath
 */
public class DiffTest {

    public DiffTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of diff method, of class Diff.
     */
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testDiff() {
        System.out.println("diff");
        Diff expResult = new Diff();
        expResult.removed = Arrays.asList("B");
        expResult.added = Arrays.asList("C");
        expResult.modified = Arrays.asList();
        expResult.same = Arrays.asList("A");

        Comparator<String> caseInsensitive = new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }

        };

        List<String> original = Arrays.asList("A", "B");
        List<String> changed = Arrays.asList("A", "C");

        Diff<String> result = Diff.diff(original, changed, caseInsensitive, null);

        System.out.println("Deleted: " + result.removed + " vs " + expResult.removed);
        System.out.println("New: " + result.added + " vs " + expResult.added);
        System.out.println("Modified: " + result.modified + " vs " + expResult.modified);
        System.out.println("Same: " + result.same + " vs " + expResult.same);

        assertEquals(expResult, result);
    }

    private static final Logger LOG = Logger.getLogger(DiffTest.class.getName());

}
