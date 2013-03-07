package org.springframework.data.simpledb.parser;

import org.junit.Test;

public class SimpleDBParserFromTest {

	@Test
	public void should_validate_from_simple_domain_name() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from test");
		parser.selectQuery();
	}

	@Test
	public void should_validate_from_simple_domain_name_with_backticks() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test`");
		parser.selectQuery();
	}

	@Test
	public void should_validate_from_prefixed_domain_name_with_backticks() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test.simpleUser`");
		parser.selectQuery();
	}

	@Test(expected = ParseException.class)
	public void should_fail_from_prefixed_domain_name_without_backticks() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from test.simpleUser");
		parser.selectQuery();
	}

}
