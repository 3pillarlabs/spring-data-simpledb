package org.springframework.data.simpledb.util;

import static junit.framework.TestCase.*;

import org.junit.Test;

public class AlphanumStringComparatorTest {

	@Test
	public void should_correctly_compare_alphanumerical_strings() throws Exception {
		AlphanumStringComparator comparator = new AlphanumStringComparator();
		assertTrue(comparator.compare("10@foo", "2@foo") > 0);
		assertTrue(comparator.compare("2@foo", "10@foo") < 0);
		assertTrue(comparator.compare("10@foo", "10@foo") == 0);
	}

}
