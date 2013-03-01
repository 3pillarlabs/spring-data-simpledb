package org.springframework.data.simpledb.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class SimpleDbIndexByQueryMethodBindedTest {

    @Test
    public void bindIndexPositionParameters_should_construct_correct_query_from_annotated_query_clauses() throws Exception {
    	final int age = 23;
    	final String email = "asd@g.c";
    	final String convertedAge = SimpleDBAttributeConverter.encode(age);
    	
    	final SimpleDbQueryMethod queryMethofd = prepareQueryMethodToTest("selectOrderedParameters", SampleEntity.class);
    	final String toProcessParsedQuery = queryMethofd.getAnnotatedQuery();
    	
//	     @Query(select = {"item_id", "sampleAttribute"}, where = "sampleAttribute<=? and item_id = ? ")
        final String expectedQuery = "select itemName(), `sampleAttribute` from `testDB.sampleEntity` where `sampleAttribute`<='" + convertedAge + "' and item_id = '" + email + "'";
        
        final String resultedQuery = QueryUtils.bindIndexPositionParameters(toProcessParsedQuery, age, email);
        
        assertThat(resultedQuery, is(expectedQuery));
    }
    
    @Test(expected = MappingException.class)
    public void bindIndexPositionParameters_should_fail_for_tricky_indexby_query_parameters() throws Exception {
    	final int age = 23;
    	final String email = "asd@g.c";
    	
    	final SimpleDbQueryMethod queryMethofd = prepareQueryMethodToTest("selectWithWrongTrickyIndexByParameters", SampleEntity.class);
    	final String toProcessParsedQuery = queryMethofd.getAnnotatedQuery();
    	
//      @Query(select = {"item_id", "sampleAttribute"}, where = " item_id <= ? <= ? or sampleAttribute = ? ")
        QueryUtils.bindIndexPositionParameters(toProcessParsedQuery, age, email);
    }
    
   public interface AnnotatedQueryRepository {
	     @Query(select = {"item_id", "sampleAttribute"}, where = "`sampleAttribute`<=? and item_id = ? ")
	        List<SampleEntity> selectOrderedParameters();
	        
	        @Query(select = {"item_id", "sampleAttribute"}, where = " item_id <= ? <= ? or sampleAttribute = ? ")
	        List<SampleEntity> selectWithWrongTrickyIndexByParameters();
   }
    
    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception {
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class) entityClass);

        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class) entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
}
