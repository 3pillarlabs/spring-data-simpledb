package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SimpleDbQueryMethodWithSelectAndWhereClauseTest {
    @Test
    public void getAnnotatedQuery_should_returned_completed_where_clause_in_query() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAll", SampleEntity.class);
        assertEquals("select itemName(), `sampleAttribute` from `testDB.sampleEntity` where `sampleAttribute`<='3' and itemName()= `5`", repositoryMethod.getAnnotatedQuery());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getAnnotpatedQuery_should_returne_error_not_all_where_parameters_in_select_clause() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectParamInWhereNotPresentInSelect", SampleEntity.class);
        repositoryMethod.getAnnotatedQuery();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getAnnotatedQuery_should_return_error_wrong_parameters() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllWrongParameter", SampleEntity.class);
        repositoryMethod.getAnnotatedQuery();
    }

    public interface AnnotatedQueryRepository {
        @Query(select = {"item_id", "sampleAttribute"}, where = {"sampleAttribute<='3'", "item_id = `5`"})
        List<SampleEntity> selectAll();
        
        @Query(value = "select *", where = {"sampleAttribute<='3'", "sampleList is ''"})
        List<SampleEntity> selectAllWrongParameter();
        
        @Query(select = {"item_id"}, where = {"sampleAttribute<='3'", "item_id = `5`"})
        List<SampleEntity> selectParamInWhereNotPresentInSelect();
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
}
