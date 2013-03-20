package org.springframework.data.simpledb.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;
import org.springframework.data.simpledb.attributeutil.SimpleDBAttributeConverter;

/**
 * @author cclaudiu
 */
public class QueryUtilsIndexByQueryTest {

	private static final String BIND_QUERY = "select * from customer_all WHERE age = ? and email = ? and balance = ?";

	@Test
	public void bindIndexPositionParameters_should_construct_correct_query_with_converted_parameter_values() {
		final int age = 23;
		final String email = "asd@g.c";
		final float balance = 12.1f;

		final String convertedAge = SimpleDBAttributeConverter.encode(age);
		final String convertedBalance = SimpleDBAttributeConverter.encode(balance);

		String expectedQuery = "select * from customer_all WHERE age = '" + convertedAge + "' and email = '" + email
				+ "' and balance = '" + convertedBalance + "'";
		String resultedQuery = QueryUtils.bindIndexPositionParameters(BIND_QUERY, age, email, balance);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void bindIndexPositionParameters_should_construct_correct_query_for_Date_parameter() {
		final String bindQueryWithDate = "select * from customer_all WHERE date = ?";

		final Date date = new Date();
		final String convertedDate = SimpleDBAttributeConverter.encode(date);

		String expectedQuery = "select * from customer_all WHERE date = '" + convertedDate + "'";
		String resultedQuery = QueryUtils.bindIndexPositionParameters(bindQueryWithDate, date);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void bindIndexPositionParameters_should_construct_correct_query_for_primitive_array_parameter() {
		final String bindQueryWithDate = "select * from customer_all WHERE byte_array = ?";

		final byte[] byteArray = new byte[] { 1, 2, 5 };
		final String convertedByteArray = SimpleDBAttributeConverter.encode(byteArray);

		String expectedQuery = "select * from customer_all WHERE byte_array = '" + convertedByteArray + "'";
		String resultedQuery = QueryUtils.bindIndexPositionParameters(bindQueryWithDate, byteArray);

		assertThat(resultedQuery, is(expectedQuery));
	}

}
