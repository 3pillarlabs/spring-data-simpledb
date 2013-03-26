package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
		final Parameters parameters = getMockParameters(new String[]{":name", ":age"}, new Class[]{String.class, int[].class});

		String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, "name", new int[]{firstAge, secondAge});

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
        final Parameters parameters = getMockParameters(new String[]{":age"}, new Class[]{String.class});

        String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, new int[]{firstAge, secondAge});

        assertThat(resultedQuery, is(expectedQuery));
    }

	static final List<Class<?>> TYPES = Arrays.<Class<?>>asList(Pageable.class, Sort.class);

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
