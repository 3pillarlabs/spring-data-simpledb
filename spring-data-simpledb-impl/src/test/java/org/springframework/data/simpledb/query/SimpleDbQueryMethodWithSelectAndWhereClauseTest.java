package org.springframework.data.simpledb.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;

public class SimpleDbQueryMethodWithSelectAndWhereClauseTest {
    @Test
    public void getAnnotatedQuery_should_returned_completed_where_clause_in_query() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectPartialFieldsWithWhereClause", SampleEntity.class);
        
//        @Query(select = {"item_id", "sampleAttribute"}, where = "sampleAttribute<='3' or item_id = `5`")
        String expectedResult = "select item_id, sampleAttribute from `testDB.sampleEntity` where sampleAttribute<='3' or item_id = `5`";

        assertEquals(expectedResult, repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_work_for_empty_where_clause() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectWithEmptyWhereClause", SampleEntity.class);
        String expectedResult = "select item_id from `testDB.sampleEntity`";
        assertEquals(expectedResult, repositoryMethod.getAnnotatedQuery());
    }
    
    @Test
    public void getAnnotatedQuery_should_work_with_repetitive_field_in_where_statement() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("whereTricky", SampleEntity.class);

//        @Query(select = {"item_id"}, where = "item_id >= `3` and item_id <= `5`")
        String expectedResult = "select item_id from `testDB.sampleEntity` where item_id >= `3` and item_id <= `5`";

        assertEquals(expectedResult, repositoryMethod.getAnnotatedQuery());
    }
    
    @Test
    public void getAnnotatedQuery_should_work_with_repetitive_field_in_select_statement() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectTricky", SampleEntity.class);

//        @Query(select = {"item_id", "item_id" }, where = "item_id >= `3` and item_id <= `5`")
        String expectedResult = "select item_id, item_id from `testDB.sampleEntity` where item_id >= `3` and item_id <= `5`";

        assertEquals(expectedResult, repositoryMethod.getAnnotatedQuery());
    }

    public interface AnnotatedQueryRepository {
        @Query(select = {"item_id", "sampleAttribute"}, where = "sampleAttribute<='3' or item_id = `5`")
        List<SampleEntity> selectPartialFieldsWithWhereClause();
        
        @Query(select = {"item_id"}, where = "")
        List<SampleEntity> selectWithEmptyWhereClause();
        
        @Query(select = {"item_id"}, where = "item_id >= `3` and item_id <= `5`")
        List<SampleEntity> whereTricky();
        
        @Query(select = {"item_id", "item_id" }, where = "item_id >= `3` and item_id <= `5`")
        List<SampleEntity> selectTricky();
        
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
}
