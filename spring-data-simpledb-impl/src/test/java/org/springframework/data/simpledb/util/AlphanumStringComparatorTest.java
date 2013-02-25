package org.springframework.data.simpledb.util;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class AlphanumStringComparatorTest {

    @Test
    public void should_correctly_compare_alphanumerical_strings() throws Exception {
        AlphanumStringComparator comparator = new AlphanumStringComparator();

        int result = comparator.compare("foo@10", "foo@2");

        assertTrue(result > 0);

    }


    @Test
    public void should_correctly_compare_long_alphanumerical_strings() throws Exception {
        AlphanumStringComparator comparator = new AlphanumStringComparator();

        int result = comparator.compare("foo@10abcd1", "foo@10abcd10");

        assertTrue(result < 0);

    }
}
