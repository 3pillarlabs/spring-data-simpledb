package org.springframework.data.simpledb.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;

public class SimpleDbQueryMethodWithWhereClauseTest {
    @Test
    public void getAnnotatedQuery_should_returned_completed_where_clause_in_query() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithWhereClause", SampleEntity.class);
        assertEquals("select * from `testDB.sampleEntity` where `sampleAttribute`<='3' or `sampleList` is ''", repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_change_id_in_where_clause() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectChangeId", SampleEntity.class);
        assertEquals("select * from `testDB.sampleEntity` where itemName() ='Item_0'", repositoryMethod.getAnnotatedQuery());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotatedQuery_should_fail_for_tricky_where_clauses() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectTrickyWhereClause", SampleEntity.class);
        repositoryMethod.getAnnotatedQuery();
    }

    public interface AnnotatedQueryRepository {
        @Query(where = "sampleAttribute<='3' or sampleList is ''")
        List<SampleEntity> selectWithWhereClause();

        @Query(where = "item_id ='Item_0'")
        List<SampleEntity> selectChangeId();
        
        @Query(where = "sampleAttribute'")
        List<SampleEntity> selectTrickyWhereClause();
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
    
}
