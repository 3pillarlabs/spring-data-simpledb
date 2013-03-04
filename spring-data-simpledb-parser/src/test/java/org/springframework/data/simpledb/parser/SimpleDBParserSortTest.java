package org.springframework.data.simpledb.parser;

import org.junit.Test;

public class SimpleDBParserSortTest {

	@Test
	public void should_validate_sort_without_specified_order() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where attr is not null order by attr");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_sort_asc_order() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where attr is not null order by attr asc");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_sort_desc_order() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where attr is not null order by attr desc");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_sort_with_itemName() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where itemName() is not null order by itemName() desc");
		parser.selectQuery();
	}
	
	@Test
	public void should_validate_sort_with_nested_attr() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where `attr.nested` is not null order by `attr.nested` desc");
		parser.selectQuery();
	}
	
	@Test(expected = ParseException.class)
	public void should_fail_for_malformed_sort() throws ParseException {
		final SimpleDBParser parser = new SimpleDBParser("select * from `test` where itemName() is not null orderby itemName() desc");
		parser.selectQuery();
	}
}
