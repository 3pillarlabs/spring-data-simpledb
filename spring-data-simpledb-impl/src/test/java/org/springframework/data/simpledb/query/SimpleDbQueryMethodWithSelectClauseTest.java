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
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectPartialFields", SampleEntity.class);
        assertEquals("select `sampleAttribute`, `sampleList` from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }

    @Test
    public void getAnnotatedQuery_should_convert_id() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectById", SampleEntity.class);
        assertEquals("select itemName() from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotatedQuery_should_fail_for_unknown_attribute() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectByWrongAttribute", SampleEntity.class);
        repositoryMethod.getAnnotatedQuery();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getAnnotatedQuery_should_fail_for_unknown_attributes() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectByWrongAttributes", SampleEntity.class);
        repositoryMethod.getAnnotatedQuery();
    }

    @Test
    public void getAnnotatedQuery_should_work_for_empty_select_statements_string() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectByEmptyString", SampleEntity.class);
        assertEquals("select * from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAnnotatedQuery_should_fail_for_tricky_select_statements_string() throws Exception {
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("selectByTrickyString", SampleEntity.class);
        assertEquals("select * from `testDB.sampleEntity`", repositoryMethod.getAnnotatedQuery());
    }
    
    public interface AnnotatedQueryRepository {
        @Query(select = {"sampleAttribute", "sampleList"})
        List<SampleEntity> selectPartialFields();

        @Query(select = "item_id")
        List<SampleEntity> selectById();
        
        @Query(select = "wrongAttribute")
        List<SampleEntity> selectByWrongAttribute();
        
        @Query(select = "3, 3.5")
        List<SampleEntity> selectByWrongAttributes();
        
        @Query(select = "")
        List<SampleEntity> selectByEmptyString();
        
        @Query(select = "select item_id,")
        List<SampleEntity> selectByTrickyString();
    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
    
}
