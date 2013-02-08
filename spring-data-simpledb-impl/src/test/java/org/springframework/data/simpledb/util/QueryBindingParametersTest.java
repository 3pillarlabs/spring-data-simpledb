package org.springframework.data.simpledb.util;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QueryBindingParametersTest {

    private static final String BIND_QUERY = "SELECT * FROM customer_all where customer_id = ? and email_id = ? and id = ?";
    private static final String NAMED_PARAMS_QUERY = "SELECT * FROM customer_all where customer_id = :customer_id and email_id = :email_id";

    @Test
    public void replaceBindParameters_should_construct_correct_query_from_bind_parameters() {
        String expectedQuery = "SELECT * FROM customer_all where customer_id = 'first' and email_id = 'second' and id = 'third'";
        String resultedQuery = QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third");
        System.out.println(resultedQuery);

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Ignore
    @Test
    public void replaceBindParameters_should_construct_correct_query_from_named_parameters() {
        String expectedQuery = "SELECT * FROM customer_all where customer_id = first and email_id = second";
        String resultedQuery = QueryUtils.bindIndexPositionParameters(NAMED_PARAMS_QUERY, "first", "second");

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void replaceBindParameters_with_wrong_number_of_params_should_throw_exception() {
        QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third", "forth");
    }

    @Ignore
    @Test public void bindNamedParameters_should_return_a_formatted_query_back_to_caller() {
        final String expectedQuery = "SELECT * FROM customer_all where customer_id = spring and email_id = data";
        SimpleDbRepositoryQuery query = new SimpleDbRepositoryQuery(null, null);
        String resultedQuery = QueryUtils.bindNamedParameters(null, "spring", "data");

        assertThat(resultedQuery, is(expectedQuery));
    }
}
