package org.springframework.data.simpledb.core;

import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {

    @Test
    public void should_create_correct_queries_if_no_other_clauses_are_specified() throws Exception {
        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());

        String returnedQuery = builder.toString();

        assertEquals("select * from `simpleDbUser`", returnedQuery);

    }

}
