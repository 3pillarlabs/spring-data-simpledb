package org.springframework.data.simpledb.parser;

import org.junit.Test;

public class SimpleDBParserLimitTest {

	@Test
	public void should_validate_limit() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` limit 12");
		parser.selectQuery();
	}

	@Test(expected = ParseException.class)
	public void should_fail_for_limit_without_number() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` limit");
		parser.selectQuery();
	}

	@Test(expected = ParseException.class)
	public void should_fail_for_limit_with_quoted_number() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` limit '12'");
		parser.selectQuery();
	}
}
