package org.springframework.data.simpledb.query.executions;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbDomain;
import org.springframework.data.simpledb.query.SampleEntity;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;

public class PagedResultExecutionTest {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doExecute_should_return_Page_type() throws Exception {
		final SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllIntoPage", SampleEntity.class);
		final PagedResultExecution execution = new PagedResultExecution(null);

		final SimpleDbQueryRunner queryRunner = Mockito.mock(SimpleDbQueryRunner.class);
		when(queryRunner.executePagedQuery()).thenReturn(new PageImpl(new ArrayList()));

		final Object result = execution.doExecute(repositoryMethod, queryRunner);

		assertTrue(Page.class.isAssignableFrom(result.getClass()));
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doExecute_should_return_List_type() throws Exception {
		final SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllIntoList", SampleEntity.class);
		final PagedResultExecution execution = new PagedResultExecution(null);

		final SimpleDbQueryRunner queryRunner = Mockito.mock(SimpleDbQueryRunner.class);
		when(queryRunner.executePagedQuery()).thenReturn(new PageImpl(new ArrayList()));

		final Object result = execution.doExecute(repositoryMethod, queryRunner);

		assertTrue(List.class.isAssignableFrom(result.getClass()));
	}

	public interface PagedAnnotatedQueryRepository {

		@Query(value = "select * from `testDB.simpleDbUser`")
		Page<SampleEntity> selectAllIntoPage(Pageable pageable);

		@Query(value = "select * from `testDB.simpleDbUser`")
		List<SampleEntity> selectAllIntoList(Pageable pageable);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
		RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
		when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);
		Method testMethod = PagedAnnotatedQueryRepository.class.getMethod(methodName, Pageable.class);
		when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
		SimpleDbDomain simpleDbDomain = new SimpleDbDomain();
		return new SimpleDbQueryMethod(testMethod, repositoryMetadata, simpleDbDomain);
	}
}
