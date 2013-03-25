package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.attributeutil.SimpleDBAttributeConverter;
import org.springframework.data.simpledb.core.SimpleDbDomain;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class SimpleDbIndexByQueryMethodBindedTest {

	@Test
	public void bindIndexPositionParameters_should_construct_correct_query_from_annotated_query_clauses()
			throws Exception {
		final int age = 23;
		final String email = "asd@g.c";
		final String convertedAge = SimpleDBAttributeConverter.encode(age);

		final SimpleDbQueryMethod queryMethod = prepareQueryMethodToTest("selectOrderedParameters", SampleEntity.class);
		final String toProcessParsedQuery = queryMethod.getAnnotatedQuery();

		// @Query(select = {"item_id", "sampleAttribute"}, where = "sampleAttribute<=? and item_id = ? ")
		final String expectedQuery = "select item_id, sampleAttribute from `testDB.sampleEntity` where `sampleAttribute`<='"
				+ convertedAge + "' and item_id = '" + email + "'";

        final Parameters parameters = getMockParameters(new String[]{"?","?"}, new Class[]{int.class, String.class, String.class});
		final String resultedQuery = QueryUtils.buildQuery(toProcessParsedQuery, parameters, age, email);

		assertThat(resultedQuery, is(expectedQuery));
	}

	@Test
	public void bindIndexPositionParameters_should_fail_for_tricky_indexby_query_parameters() throws Exception {
		final int age = 23;
		final String email = "asd@g.c";

		final SimpleDbQueryMethod queryMethod = prepareQueryMethodToTest("selectWithWrongTrickyIndexByParameters",
				SampleEntity.class);
		final String toProcessParsedQuery = queryMethod.getAnnotatedQuery();

		// @Query(select = {"item_id", "sampleAttribute"}, where = " item_id <= ? <= ? or sampleAttribute = ? ")
        final Parameters parameters = getMockParameters(new String[]{"?","?"}, new Class[]{int.class, String.class});
        final String resultedQuery = QueryUtils.buildQuery(toProcessParsedQuery, parameters, age, email);
	}

	public interface AnnotatedQueryRepository {

		@Query(select = { "item_id", "sampleAttribute" }, where = "`sampleAttribute`<=? and item_id = ? ")
		List<SampleEntity> selectOrderedParameters();

		@Query(select = { "item_id", "sampleAttribute" }, where = " item_id <= ? <= ? or sampleAttribute = ? ")
		List<SampleEntity> selectWithWrongTrickyIndexByParameters();
	}

	private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
		RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
		when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

		Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
		when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
		SimpleDbDomain simpleDbDomain = new SimpleDbDomain();
		return new SimpleDbQueryMethod(testMethod, repositoryMetadata, simpleDbDomain);
	}

    static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Parameter getMockParameter(String placeHolder, Integer idx, Class clazz) {
        Parameter mockParameter = Mockito.mock(Parameter.class);

        Mockito.when(mockParameter.getPlaceholder()).thenReturn(placeHolder);
        Mockito.when(mockParameter.isNamedParameter()).thenReturn(Boolean.FALSE);
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
