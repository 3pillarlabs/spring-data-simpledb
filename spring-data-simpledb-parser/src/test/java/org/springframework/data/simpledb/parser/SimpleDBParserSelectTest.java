package org.springframework.data.simpledb.parser;

import org.junit.Test;

public class SimpleDBParserSelectTest {

	@Test
	public void should_validate_select_all() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test`");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_select_item() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select itemName() from `test`");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_select_count() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select count(*) from `test`");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_select_list_off_attributes() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select attr1,attr2, attr3 from `test`");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_select_list_off_attributes_with_backticks() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select `attr1.asfz`,attr2, attr3 from `test`");
		parser.selectQuery();
	}

	@Test(expected = ParseException.class)
	public void should_fail_select_list_off_nested_attributes_without_backticks() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select attr1.asfz,attr2, attr3 from `test`");
		parser.selectQuery();
	}
	
	@Test(expected = ParseException.class)
	public void should_fail_malformed_select_list_off_attributes() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select *,attr1,attr2, attr3 from `test`");
		parser.selectQuery();
	}
	
}
