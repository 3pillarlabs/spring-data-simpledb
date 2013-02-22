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
        assertEquals("select * from `testDB.sampleEntity` where `sampleAttribute`<='3' and `sampleList` is ''", repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_returned_completed_select_clause_in_query_and_change_id() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllChangeId", SampleEntity.class);
        assertEquals("select * from `testDB.sampleEntity` where itemName()='Item_0'", repositoryMethod.getAnnotatedQuery());
    }

    public interface AnnotatedQueryRepository {
        @Query(where = {"sampleAttribute<='3'", "sampleList is ''"})
        List<SampleEntity> selectAll();

        @Query(where = "item_id  ='Item_0'")
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
