package org.springframework.data.simpledb.attributeutil;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimpleDbAttributeKeySplitterTest {

	@Test
	public void convertKey_should_include_split_prefixes() throws Exception {
		String rawKey = "test";
		String convertedKey = SimpleDbAttributeKeySplitter.convertKey(rawKey, 0);

		assertThat(convertedKey, containsString(SimpleDbAttributeKeySplitter.SPLIT_ATTRIBUTE_PREFIX_START));
	}

}
