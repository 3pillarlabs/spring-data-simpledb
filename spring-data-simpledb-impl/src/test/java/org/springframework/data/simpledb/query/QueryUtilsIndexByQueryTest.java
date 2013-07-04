package org.springframework.data.simpledb.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
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
        final Parameters parameters = getMockParameters(new String[]{"?","?","?"}, new Class[]{int.class, String.class, float.class});

		String resultedQuery = QueryUtils.buildQuery(BIND_QUERY, parameters, age, email, balance);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void bindIndexPositionParameters_should_construct_correct_query_for_Date_parameter() {
		final String bindQueryWithDate = "select * from customer_all WHERE date = ?";

		final Date date = new Date();
		final String convertedDate = SimpleDBAttributeConverter.encode(date);

		String expectedQuery = "select * from customer_all WHERE date = '" + convertedDate + "'";
        final Parameters parameters = getMockParameters(new String[]{"?"}, new Class[]{Date.class});
		String resultedQuery = QueryUtils.buildQuery(bindQueryWithDate, parameters, date);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
     public void bindIndexPositionParameters_should_construct_correct_query_for_primitive_array_parameter() {
        final String bindQueryWithDate = "select * from customer_all WHERE byte_array = ?";

        final byte[] byteArray = new byte[] { 1, 2, 5 };
        final String convertedByteArray = SimpleDBAttributeConverter.encode(byteArray);

        String expectedQuery = "select * from customer_all WHERE byte_array = '" + convertedByteArray + "'";
        final Parameters parameters = getMockParameters(new String[]{"?"}, new Class[]{byte[].class});
        String resultedQuery = QueryUtils.buildQuery(bindQueryWithDate, parameters, byteArray);

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test
    public void bindIndexPositionParameters() {
        final String bindQueryWithDate = "select * from customer_all WHERE byte_array = ? and c = 4";

        int intValue = 5;
        final String convertedInt = SimpleDBAttributeConverter.encode(intValue);

        String expectedQuery = "select * from customer_all WHERE byte_array = '" + convertedInt + "' and c = 4";
        final Parameters parameters = getMockParameters(new String[]{"?"}, new Class[]{int.class});
        String resultedQuery = QueryUtils.buildQuery(bindQueryWithDate, parameters, intValue);

        assertThat(resultedQuery, is(expectedQuery));
    }

	@Test
	public void buildQueryConditionsWithParameters_should_construct_correct_bind_query_for_in_operator() {
		final String bind_query = "select * from customer_all WHERE age in ?";

		final long firstAge = 23;
		final long secondAge = 25;
		final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);
		final String convertedSecondAge = SimpleDBAttributeConverter.encode(secondAge);

		String expectedQuery = "select * from customer_all WHERE age in ('" + convertedFirstAge + "','"
				+ convertedSecondAge + "')";

		final Parameters parameters = getMockParameters(new String[] { "?" }, new Class[] { long[].class });
		String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, new long[] { firstAge, secondAge });

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void buildQueryConditionsWithParameters_should_construct_tricky_query_for_in_operator() {
		final String bind_query = "select * from customer_all WHERE age in ('1','2') and age=?";

		final long firstAge = 23;
		final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);

		String expectedQuery = "select * from customer_all WHERE age in ('1','2') and age='" + convertedFirstAge + "'";

		final Parameters parameters = getMockParameters(new String[]{"?"}, new Class[]{long.class});
		String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, firstAge);

		assertThat(resultedQuery, is(expectedQuery));
	}

	/**
	 * This test shows that if there is a String containing a '?' character, this character will be recognised as a
	 * index parameter
	 */

	@Ignore
	@Test(expected = MappingException.class)
	public void buildQueryConditionsWithParameters_should_fail_for_wrong_string() {
		final String bind_query = "select * from customer_all WHERE name = 'where?' and age=?";
		final Parameters parameters = getMockParameters("?");
		System.out.println(QueryUtils.buildQuery(bind_query, parameters, 23));
	}

	/**
	 * This test shows that a index parameter should not be in quotes
	 * 
	 * @Test(expected = MappingException.class)
	 */
	public void buildQueryConditionsWithParameters_should_double_quote_for_already_quoted_parameter() {
		final String bind_query = "select * from customer_all WHERE name = '?'";

		final Parameters parameters = getMockParameters("?");
		QueryUtils.buildQuery(bind_query, parameters, 23);
	}

	/**
	 * This test shows that if a named param or an index param is used to replace a field name, this will generate an
	 * invalid query because quotes are added surrounding the field name
	 */
	@Ignore
	@Test
	public void buildQueryConditionsWithParameters_should_construct_The_trickiest_query_for_in_operator() {
		final String bind_query = "select * from customer_all WHERE ?=?";

		final long firstAge = 23;
		final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);

		String expectedQuery = "select * from customer_all WHERE name = '" + convertedFirstAge + "'";

		final Parameters parameters = getMockParameters("?", "?");
		String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, "name", firstAge);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void replaceOneParameterInQuery_should_parse_positional_parameters() {
		
		final String paramPlaceholder = "\\?";
		final String rawQuery = "a = ? AND (`b.c` = ? OR d IN ?) ORDER BY x";
		
		String replacedQuery = QueryUtils.replaceOneParameterInQuery(rawQuery, 
				paramPlaceholder, 0.01F);
		replacedQuery = QueryUtils.replaceOneParameterInQuery(replacedQuery, 
				paramPlaceholder, "baz");
		replacedQuery = QueryUtils.replaceOneParameterInQuery( replacedQuery,
				paramPlaceholder, new String[] {"foo", "bar"});
		
		assertEquals("a = '" + SimpleDBAttributeConverter.encode(0.01F) + 
				"' AND (`b.c` = 'baz' OR d IN ('foo','bar')) ORDER BY x", replacedQuery);
	}
	
    static final List<Class<?>> TYPES = Arrays.<Class<?>>asList(Pageable.class, Sort.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Parameter getMockParameter(String placeHolder, Integer idx, Class clazz) {
        Parameter mockParameter = Mockito.mock(Parameter.class);

        Mockito.when(mockParameter.getPlaceholder()).thenReturn(placeHolder);
        Mockito.when(mockParameter.isNamedParameter()).thenReturn(Boolean.FALSE);
        Mockito.when(mockParameter.getIndex()).thenReturn(idx);
        Mockito.when(mockParameter.getType()).thenReturn(clazz);
        Mockito.when(mockParameter.getType()).thenReturn(clazz);
        Mockito.when(mockParameter.isSpecialParameter()).thenReturn(TYPES.contains(clazz));

        return mockParameter;
    }

    private Parameters getMockParameters(String... placeHolders) {
        return getMockParameters(placeHolders, new Class[placeHolders.length]);
    }

    @SuppressWarnings({ "rawtypes" })
    private Parameters getMockParameters(String[] placeHolders, Class[] clazzes) {
        Parameters mockParameters = Mockito.mock(Parameters.class);

        List<Parameter> parameters = new ArrayList<Parameter>(placeHolders.length);
        for(int idx = 0; idx < placeHolders.length; ++idx) {
            parameters.add(getMockParameter(placeHolders[idx], idx, clazzes[idx]));
        }

        Mockito.when(mockParameters.iterator()).thenReturn(parameters.iterator());
        Mockito.when(mockParameters.getNumberOfParameters()).thenReturn(parameters.size());

        return mockParameters;
    }

}
