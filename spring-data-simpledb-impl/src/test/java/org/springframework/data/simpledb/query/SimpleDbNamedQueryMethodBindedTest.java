package org.springframework.data.simpledb.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class SimpleDbNamedQueryMethodBindedTest {

	// Methods testing the Named Query Replacement via @Query.select && @Query.where() clauses
	@Test public void buildQueryConditionsWithParameters_should_work_with_the_same_placeholder_values_as_fieldkeys() throws Exception {
        // @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:attribute", "item_id = :item"})

		final String expectedQuery = "select itemName(), `sampleAttribute` from `testDB.sampleEntity` where `sampleAttribute`<='3' and itemName() = '5'";
		
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithEqualPlaceholders", SampleEntity.class);
		final String toProcessRawQuery = repositoryMethod.getAnnotatedQuery();

		final Parameters parameters = getMockParameters(":sampleAttribute", ":item_id");
		
		String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(toProcessRawQuery, parameters, "3", "5");

		assertThat(resultedQuery, is(expectedQuery));
	}
	
	@Test public void buildQueryConditionsWithParameters_should_work_with_different_placeholder_values_as_fieldkeys() throws Exception {
//        @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:sampleAttribute", "item_id = :item_id"})

		final String expectedQuery = "select itemName(), `sampleAttribute` from `testDB.sampleEntity` where `sampleAttribute`<='3' and itemName() = '5'";
		
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithDifferentPlaceholders", SampleEntity.class);
		final String toProcessRawQuery = repositoryMethod.getAnnotatedQuery();

		final Parameters parameters = getMockParameters(":attribute", ":item");
		
		String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(toProcessRawQuery, parameters, "3", "5");

		assertThat(resultedQuery, is(expectedQuery));
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void buildQueryConditionsWithParameters_should_fail_for_wrong_namedquery_parameters() throws Exception {
//      @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:sampleAttribute :otherAttribute", "item_id = :item_id"})
		
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithTrickyNamedParameters", SampleEntity.class);
		final String toProcessRawQuery = repositoryMethod.getAnnotatedQuery();

		final Parameters parameters = getMockParameters(":attribute", ":item");
		
		QueryUtils.buildQueryConditionsWithParameters(toProcessRawQuery, parameters, "3", "5");
	}
	
	@Test public void buildQueryConditionsWithParameters_should_work_with_primitive_type_parameter_values() throws Exception {
//      @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:sampleAttribute", "item_id = :item_id"})

		final int age = 5;
    	final String convertedAge = SimpleDBAttributeConverter.encode(age);

		final String expectedQuery = "select itemName(), `sampleAttribute` from `testDB.sampleEntity` where `sampleAttribute`<='3' and itemName() = '" + convertedAge + "'";
		
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithDifferentPlaceholders", SampleEntity.class);
		final String toProcessRawQuery = repositoryMethod.getAnnotatedQuery();

		final Parameters parameters = getMockParameters(":attribute", ":item");
		
		String resultedQuery = QueryUtils.buildQueryConditionsWithParameters(toProcessRawQuery, parameters, "3", 5);

		assertThat(resultedQuery, is(expectedQuery));
	}
	
	// -------------------------- define methods to work with; Note that the methods arguments are mocked  ----------------------- //
    public interface AnnotatedQueryRepository {
    	
        @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:attribute", "item_id = :item"})
        List<SampleEntity> selectWithDifferentPlaceholders();
        
        @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:sampleAttribute", "item_id = :item_id"})
        List<SampleEntity> selectWithEqualPlaceholders();
        
        @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<=:sampleAttribute :otherAttribute", "item_id = :item_id"})
        List<SampleEntity> selectWithTrickyNamedParameters();
    }
    
	
    // -------------------------------- Reused/Mocks --------------------------------- //
	static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);

	private Parameter getMockParameter(String placeHolder, Integer idx, Class clazz){
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

	@SuppressWarnings({"rawtypes"})
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
	
    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
}