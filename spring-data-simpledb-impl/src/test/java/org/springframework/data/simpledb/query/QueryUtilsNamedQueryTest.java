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
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryUtilsNamedQueryTest {

	@Test
	public void buildQueryConditionsWithParameters_should_work_with_WHERE_clause() {
		final String expectedQuery = "select * from spring_data where type = 'spring-type'";
		final String rawQuery = "select * from spring_data where type = :type";
		final Parameters parameters = getMockParameters(new String[]{":type"}, new Class[]{String.class});

		String resultedQuery = QueryUtils.buildQuery(rawQuery, parameters, "spring-type");

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void buildQueryConditionsWithParameters_should_return_a_formatted_query() {
		final String expectedQuery = "select * from spring_data where name = 'spring-name' and type = 'spring-type' or location = 'Timisoara'";
		final String rawQuery = "select * from spring_data where name = :name and type = :type or location = :location ";
		final Parameters parameters = getMockParameters(new String[]{":name", ":type", ":location"}, new Class[]{String.class, String.class, String.class});

		String resultedQuery = QueryUtils.buildQuery(rawQuery, parameters, "spring-name",
				"spring-type", "Timisoara");

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void buildQueryConditionsWithParameters_should_construct_correct_query_with_converted_parameter_values() {
		final String bind_query = "select * from customer_all WHERE age = :age and email = :email and balance = :balance";

		final int age = 23;
		final String email = "asd@g.c";
		final float balance = 12.1f;

		final String convertedAge = SimpleDBAttributeConverter.encode(age);
		final String convertedBalance = SimpleDBAttributeConverter.encode(balance);

		String expectedQuery = "select * from customer_all WHERE age = '" + convertedAge + "' and email = '" + email
				+ "' and balance = '" + convertedBalance + "'";

		final Parameters parameters = getMockParameters(new String[]{":age", ":email", ":balance"}, new Class[]{int.class, String.class, float.class});

		String resultedQuery = QueryUtils.buildQuery(bind_query, parameters, age, email,
				balance);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void buildQueryConditionsWithParameters_should_construct_correct_query_for_Date_parameter() {
		final String bindQueryWithDate = "select * from customer_all WHERE date = :date";

		final Date date = new Date();
		final String convertedDate = SimpleDBAttributeConverter.encode(date);

		String expectedQuery = "select * from customer_all WHERE date = '" + convertedDate + "'";

		final Parameters parameters = getMockParameters(new String[]{":date"}, new Class[]{String.class});

		String resultedQuery = QueryUtils.buildQuery(bindQueryWithDate, parameters, date);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void buildQueryConditionsWithParameters_should_construct_correct_query_for_primitive_array_parameter() {
		final String bindQueryWithDate = "select * from customer_all WHERE byte_array = :byte_array";

		final byte[] byteArray = new byte[] { 1, 2, 5 };
		final String convertedByteArray = SimpleDBAttributeConverter.encode(byteArray);

		String expectedQuery = "select * from customer_all WHERE byte_array = '" + convertedByteArray + "'";

		final Parameters parameters = getMockParameters(new String[]{":byte_array"}, new Class[]{String.class});

		String resultedQuery = QueryUtils.buildQuery(bindQueryWithDate, parameters, byteArray);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test(expected = MappingException.class)
	public void validateBindParametersCount_should_fail_if_wrong_number_of_parameters_and_values() {
		QueryUtils.validateBindParametersCount(getMockParameters(":param_1", ":param_2", ":param_3"), "value1");
	}

	@Test
	public void validateBindParametersTypes_should_pass_for_supported_primitive_types() {
		QueryUtils.validateBindParametersTypes(getMockParameters(new String[] { ":int", ":long", ":double", ":float",
				":boolean", ":short", ":byte" }, new Class[] { int.class, long.class, double.class, float.class,
				boolean.class, short.class, byte.class }));
	}

	@Test
	public void validateBindParametersTypes_should_pass_for_supported_core_types() {
		QueryUtils.validateBindParametersTypes(getMockParameters(new String[] { ":string", ":date", ":int", ":long",
				":double", ":float", ":boolean", ":short", ":byte" }, new Class[] { String.class, Date.class,
				Integer.class, Long.class, Double.class, Float.class, Boolean.class, Short.class, Byte.class }));
	}

	@Test
	public void validateBindParametersTypes_should_pass_for_primitive_array_types() {
		QueryUtils.validateBindParametersTypes(getMockParameters(new String[] { ":int", ":long", ":double", ":float",
				":boolean", ":short", ":byte" }, new Class[] { int[].class, long[].class, double[].class,
				float[].class, boolean[].class, short[].class, byte[].class }));
	}

	@Test
	public void validateBindParametersTypes_should_pass_for_special_types() {
		QueryUtils.validateBindParametersTypes(getMockParameters(new String[] { ":pageable", ":sort" }, new Class[] {
				Pageable.class, Sort.class }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateBindParametersTypes_should_fail_for_unsupported_types() {
		QueryUtils.validateBindParametersTypes(getMockParameters(new String[] { ":param_1" },
				new Class[] { SimpleDbSampleEntity.class }));
	}

    @Ignore
	@Test
	public void buildQueryConditionsWithParameters_should_work_with_complex_parameters() {
		final String expectedQuery = "select * from spring_data where name = 'spring-name' and type = 'spring-type'";
		final String rawQuery = "select * from spring_data where name = ::name and type = :";
		final Parameters parameters = getMockParameters(new String[]{"::name", ":"}, new Class[]{String.class, String.class});

		String resultedQuery = QueryUtils.buildQuery(rawQuery, parameters, "spring-name",
				"spring-type");

		assertThat(resultedQuery, is(expectedQuery));
	}

    @Test
    public void like_operators_should_be_wrapped_in_quotes() {
    	final String expectedQuery = "select * from spring_data where first_name like '%joe' and last_name like 'dev%' and middle_name like '%o%'";
		final String rawQuery = "select * from spring_data where first_name like %:fname and last_name like :lname% and middle_name like %:mname%";
		final Parameters parameters = getMockParameters(new String[]{":fname", ":lname", ":mname"}, new Class[]{String.class, String.class, String.class});

		String resultedQuery = QueryUtils.buildQuery(rawQuery, parameters, "joe",
				"dev", "o");

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
