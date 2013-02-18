package org.springframework.data.simpledb.query;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by: mgrozea
 */
public class SimpleDbRepositoryQueryTest {


    @Test (expected = IllegalArgumentException.class)
    public void assertNotHavingNestedQueryParameters_should_fail_for_nested_attributes() {
        SimpleDbQueryMethod method = Mockito.mock(SimpleDbQueryMethod.class);
        Mockito.when(method.getDomainClazz()).thenReturn((Class) SampleEntity.class);

        SimpleDbRepositoryQuery repositoryQuery = new SimpleDbRepositoryQuery(method, null);
        repositoryQuery.assertNotHavingNestedQueryParameters("select sampleNestedAttribute from SampleEntity");
    }
}