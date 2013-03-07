package org.springframework.data.simpledb.query;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryUtilsEscapeTest {

	@Test
	public void escapeQueryAttributes_should_convert_item_id() {
		final String convertedQuery = QueryUtils.escapeQueryAttributes("select customer_id from customers",
				"customer_id");

		assertThat(convertedQuery, is("select itemName() from customers"));
	}

	@Test
	public void escapeQueryAttributes_should_work_without_item_id() {
		final String convertedQuery = QueryUtils.escapeQueryAttributes("select * from customers", "customer_id");

		assertThat(convertedQuery, is("select * from customers"));
	}

	@Test
	public void escapeQueryAttributes_should_convert_item_id_for_complex_queries() {
		final String convertedQuery = QueryUtils.escapeQueryAttributes(
				"select customer_id from customers where customer_id > 4 and age = 3", "customer_id");

		assertThat(convertedQuery, is("select itemName() from customers where itemName() > 4 and age = 3"));
	}

	@Test
	public void escapeQueryAttributes_should_not_convert_overlapping_id_field_names() {
		final String convertedQuery = QueryUtils.escapeQueryAttributes("select itemName() from customers", "itemName");

		assertThat(convertedQuery, is("select itemName() from customers"));

	}

	@Test
	public void escapeQueryAttributes_should_convert_last_occurence_of_item_id() {
		final String convertedQuery = QueryUtils.escapeQueryAttributes("select * from customers order by itemName",
				"itemName");

		assertThat(convertedQuery, is("select * from customers order by itemName()"));
	}

}
