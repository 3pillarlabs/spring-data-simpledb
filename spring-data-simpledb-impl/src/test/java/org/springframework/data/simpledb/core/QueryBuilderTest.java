package org.springframework.data.simpledb.core;

import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class QueryBuilderTest {

    @Test
    public void should_create_correct_queries_if_no_other_clauses_are_specified() throws Exception {
        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());

        String returnedQuery = builder.toString();

        assertEquals("select * from `simpleDbSampleEntity`", returnedQuery);

    }


    @Test
    public void should_include_sort_clause_if_requested() throws Exception {
        Sort sort = new Sort(Sort.Direction.ASC, "testField");

        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());
        builder.with(sort);

        String returnedQuery = builder.toString();

        assertThat(returnedQuery, containsString("where testField is not null order by testField asc"));

    }


    @Test
    public void should_include_count_clause_if_requested() throws Exception {

        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());
        builder.with(QueryBuilder.Count.ON);

        String returnedQuery = builder.toString();

        assertThat(returnedQuery,  containsString("select count(*) from"));

    }

    @Test
    public void should_not_include_count_clause_if_requested() throws Exception {

        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());
        builder.with(QueryBuilder.Count.OFF);

        String returnedQuery = builder.toString();

        assertThat(returnedQuery, not(containsString("select count(*) from")));

    }


    @Test
    public void should_include_limited_items_if_requested() throws Exception {

        QueryBuilder builder = new QueryBuilder(SimpleDbSampleEntity.entityInformation());
        builder.with(Arrays.asList(new String[]{"id1", "id2"}));

        String returnedQuery = builder.toString();

        assertThat(returnedQuery, containsString("where (itemName()='id1' or itemName()='id2')"));

    }






}