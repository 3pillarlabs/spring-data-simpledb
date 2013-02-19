package org.springframework.data.simpledb.query.executions;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.query.SampleEntity;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 *
 */
public class MultipleResultExecutionTest {

    @Test
    public void returnsFieldOfTypeCollection_should_return_true_for_collection_field() throws Exception{
        SimpleDbQueryMethod repositoryMethod = prepareQueryMethodToTest("partialObjectListSelect", SampleEntity.class);
        MultipleResultExecution multipleResultExecution = new MultipleResultExecution(null);
        assertEquals(MultipleResultExecution.MultipleResultType.COLLECTION_OF_DOMAIN_ENTITIES, multipleResultExecution.detectResultType(repositoryMethod));
    }

    public interface AnnotatedQueryRepository {

        @Query(value = "select sampleList from `testDB.simpleDbUser` where itemName()='Item_0'")
        List<List<Object>> selectCoreFields();

        @Query(value = "SELECT sampleList FROM `testDB.simpleDbUser` where itemName()='Item_0'")
        List<SampleEntity> partialObjectListSelect();

        @Query(value = "select sampleAttribute from `testDB.simpleDbUser`")
        Set<Float> primitiveFieldSelect();

        @Query(value = "select sampleAttribute from `testDB.simpleDbUser where itemName()='Item_0'")
        int getOneSampleAttribute();

    }

    private SimpleDbQueryMethod prepareQueryMethodToTest(String methodName, Class<?> entityClass) throws Exception{
        RepositoryMetadata repositoryMetadata = Mockito.mock(RepositoryMetadata.class);
        when(repositoryMetadata.getDomainType()).thenReturn((Class)entityClass);
        Method testMethod = AnnotatedQueryRepository.class.getMethod(methodName);
        when(repositoryMetadata.getReturnedDomainClass(testMethod)).thenReturn((Class)entityClass);
        return new SimpleDbQueryMethod(testMethod, repositoryMetadata);
    }
}
