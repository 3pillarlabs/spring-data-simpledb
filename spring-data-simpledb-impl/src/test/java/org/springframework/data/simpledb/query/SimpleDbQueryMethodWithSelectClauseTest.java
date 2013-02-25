package org.springframework.data.simpledb.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;

public class SimpleDbQueryMethodWithSelectClauseTest {

    @Test
    public void getAnnotatedQuery_should_returned_completed_select_clause_in_query() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAll", SampleEntity.class);
        assertEquals("select `sampleAttribute`, `sampleList` from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_returned_completed_select_clause_in_query_and_change_id() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllChangeId", SampleEntity.class);
        assertEquals("select itemName() from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }

    public interface AnnotatedQueryRepository {
        @Query(select = {"sampleAttribute", "sampleList"})
        List<SampleEntity> selectAll();

        @Query(select = "item_id")
        List<SampleEntity> selectAllChangeId();
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
    
}
