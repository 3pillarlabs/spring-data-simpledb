package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;

import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;

//@Ignore
public class SimpleDbQueryMethodTest {
    @Test
    public void getAnnotatedQuery_should_returned_completed_where_clause_in_query(){
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAll", SampleEntity.class);
        assertEquals("select * from `testDB.simpleDbUser` where sampleAttribute='3' and sampleList=''",repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_returned_completed_where_clause_in_query_and_change_id(){
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectAllChangeId", SampleEntity.class);
        assertEquals("select * from `testDB.simpleDbUser` where itemName()='Item_0'",repositoryMethod.getAnnotatedQuery());
    }

    public interface AnnotatedQueryRepository {
        @Query(where = {"sampleAttribute='3'", "sampleList=''"})
        List<SampleEntity> selectAll();

        @Query(where = "item_id  ='Item_0'")
        List<SampleEntity> selectAllChangeId();
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass){
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class)entityClass);
        try {
            Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
            when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class)entityClass);
            return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
        } catch (NoSuchMethodException e) {
            fail("No such method");
            return null;
        }
    }
}
