package org.springframework.data.simpledb.query;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.attributeutil.SimpleDBAttributeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryUtilsArrayTest {

	@Test
	public void buildQueryConditionsWithParameters_should_construct_correct_named_query_for_in_operator() {
		final String bind_query = "select * from customer_all WHERE age in :age and x= :name";

		final int firstAge = 23;
        final int secondAge = 25;
		final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);
        final String convertedSecondAge = SimpleDBAttributeConverter.encode(secondAge);

		String expectedQuery = "select * from customer_all WHERE age in ('" + convertedFirstAge + "','"+convertedSecondAge+"') and x= 'name'";
		final Parameters parameters = getMockParameters(":name", ":age");

		String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(bind_query, parameters, "name", new int[]{firstAge, secondAge});

		assertThat(resultedQuery, is(expectedQuery));
	}

    @Test
    public void buildQueryConditionsWithParameters_should_construct_correct_named_query_for_in_operator_with_no_space() {
        final String bind_query = "select * from customer_all WHERE age in:age";

        final int firstAge = 23;
        final int secondAge = 25;
        final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);
        final String convertedSecondAge = SimpleDBAttributeConverter.encode(secondAge);

        String expectedQuery = "select * from customer_all WHERE age in('" + convertedFirstAge + "','"+convertedSecondAge+"')";
        final Parameters parameters = getMockParameters(":age");

        String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(bind_query, parameters, new int[]{firstAge, secondAge});

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test
    public void buildQueryConditionsWithParameters_should_construct_correct_bind_query_for_in_operator() {
        final String bind_query = "select * from customer_all WHERE age in ?";

        final long firstAge = 23;
        final long secondAge = 25;
        final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);
        final String convertedSecondAge = SimpleDBAttributeConverter.encode(secondAge);

        String expectedQuery = "select * from customer_all WHERE age in ('" + convertedFirstAge + "','"+convertedSecondAge+"')";

        String resultedQuery = QueryUtils.bindIndexPositionParameters(bind_query, new long[]{firstAge, secondAge});

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test
    public void buildQueryConditionsWithParameters_should_construct_tricky_query_for_in_operator() {
        final String bind_query = "select * from customer_all WHERE age in ('1','2') and age=?";

        final long firstAge = 23;
        final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);

        String expectedQuery = "select * from customer_all WHERE age in ('1','2') and age='"+convertedFirstAge+"'";

        String resultedQuery = QueryUtils.bindIndexPositionParameters(bind_query, firstAge);

        assertThat(resultedQuery, is(expectedQuery));
    }

    /**
     * This test shows that if there is a String containing a '?' character, this character will be recognised as a index parameter
     */
    @Test(expected = MappingException.class)
    public void buildQueryConditionsWithParameters_should_fail_for_wrong_string() {
        final String bind_query = "select * from customer_all WHERE name = 'where?' and age=?";
        System.out.println(QueryUtils.bindIndexPositionParameters(bind_query, 23));
    }

    /**
     * This test shows that a index parameter should not be in quotes
    @Test(expected = MappingException.class)
    */
    public void buildQueryConditionsWithParameters_should_double_quote_for_already_quoted_parameter() {
        final String bind_query = "select * from customer_all WHERE name = '?'";

        QueryUtils.bindIndexPositionParameters(bind_query, 23);
    }

    /**
     * This test shows that if a named param or an index param is used to replace a field name, this will generate an invalid query because
     * quotes are added surrounding the field name
     */
    @Ignore
    @Test
    public void buildQueryConditionsWithParameters_should_construct_The_trickiest_query_for_in_operator() {
        final String bind_query = "select * from customer_all WHERE ?=?";

        final long firstAge = 23;
        final String convertedFirstAge = SimpleDBAttributeConverter.encode(firstAge);

        String expectedQuery = "select * from customer_all WHERE name = '"+convertedFirstAge+"'";

        String resultedQuery = QueryUtils.bindIndexPositionParameters(bind_query, "name", firstAge);

        assertThat(resultedQuery, is(expectedQuery));
    }

	static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Parameter getMockParameter(String placeHolder, Integer idx, Class clazz) {
		Parameter mockParameter = Mockito.mock(Parameter.class);

		Mockito.when(mockParameter.getPlaceholder()).thenReturn(placeHolder);
		Mockito.when(mockParameter.isNamedParameter()).thenReturn(Boolean.TRUE);
		Mockito.when(mockParameter.getIndex()).thenReturn(idx);
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
