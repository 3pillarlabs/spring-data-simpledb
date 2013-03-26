package org.springframework.data.simpledb.query.executions;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbDomain;
import org.springframework.data.simpledb.query.SampleEntity;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 *
 */
public class MultipleResultExecutionTest {

	@Test
	public void detectResultType_should_return_COLLECTION_OF_DOMAIN_ENTITIES() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAll", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.COLLECTION_OF_DOMAIN_ENTITIES,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_for_selected_field_should_return_COLLECTION_OF_DOMAIN_ENTITIES() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("partialSampleListSelect", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.COLLECTION_OF_DOMAIN_ENTITIES,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_should_return_LIST_OF_LIST_OF_OBJECT() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectCoreFields", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.LIST_OF_LIST_OF_OBJECT,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_for_multiple_selected_attributes_should_return_LIST_OF_LIST_OF_OBJECT()
			throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectFields", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.LIST_OF_LIST_OF_OBJECT,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_should_return_FIELD_OF_TYPE_COLLECTION() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("sampleListSelect", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.FIELD_OF_TYPE_COLLECTION,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_should_return_LIST_OF_FIELDS() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("sampleAllSampleListSelect", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.LIST_OF_FIELDS,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_should_return_SET_OF_FIELDS() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("sampleAllSampleListSelectInSet",
				SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.SET_OF_FIELDS,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test
	public void detectResultType_should_return_FIELD_OF_TYPE_COLLECTION_of_list_of_lists() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("listOfListOfIntegerFieldSelect",
				SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		assertEquals(MultipleResultExecution.MultipleResultType.FIELD_OF_TYPE_COLLECTION,
				multipleResultExecution.detectResultType(repositoryMethod));
	}

	@Test(expected = IllegalArgumentException.class)
	public void detectResultType_should_return_error_for_inexisting_field() throws Exception {
		SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("sampleWrongField", SampleEntity.class);
		MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
		multipleResultExecution.detectResultType(repositoryMethod);
	}

	public interface AnnotatedQueryRepository {

		@Query(value = "select * from `testDB.simpleDbUser` where itemName()='Item_0'")
		List<SampleEntity> selectAll();

		@Query(value = "SELECT sampleList FROM `testDB.simpleDbUser` where itemName()='Item_0'")
		List<SampleEntity> partialSampleListSelect();

		@Query(value = "select sampleList from `testDB.simpleDbUser` where itemName()='Item_0'")
		List<List<Object>> selectCoreFields();

		@Query(value = "select sampleList, sampleList from `testDB.simpleDbUser` where itemName()='Item_0'")
		List<List<Object>> selectFields();

		@Query(value = "select sampleList from `testDB.simpleDbUser`")
		List<Integer> sampleListSelect();

		@Query(value = "select sampleList from `testDB.simpleDbUser`")
		List<List<Integer>> sampleAllSampleListSelect();

		@Query(value = "select sampleList from `testDB.simpleDbUser`")
		Set<List<Integer>> sampleAllSampleListSelectInSet();

		@Query(value = "select sampleFail from `testDB.simpleDbUser`")
		Set<List<Integer>> sampleWrongField();

		@Query(value = "select listOfListOfInteger from `testDB.simpleDbUser`")
		List<List<Integer>> listOfListOfIntegerFieldSelect();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
		RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
		when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);
		Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
		when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
		SimpleDbDomain simpleDbDomain = new SimpleDbDomain();
		return new SimpleDbQueryMethod(testMethod, repositoryMetadata, simpleDbDomain);
	}
}
