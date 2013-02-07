package org.springframework.data.simpledb.util;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QueryBindingParametersTest {

    private static final String BIND_QUERY = "SELECT * FROM customer_all where customer_id = ? and email_id = ? and id = ?";
    private static final String NAMED_PARAMS_QUERY = "SELECT * FROM customer_all where customer_id = :customer_id and email_id = :email_id";

    @Test
    public void replaceBindParameters_should_construct_correct_query_from_bind_parameters() {
        String expectedQuery = "SELECT * FROM customer_all where customer_id = first and email_id = second and id = third";
        String resultedQuery = QueryParametersBinder.bindParameters(BIND_QUERY, "first", "second", "third");

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test
    public void replaceBindParameters_should_construct_correct_query_from_named_parameters() {
        String expectedQuery = "SELECT * FROM customer_all where customer_id = first and email_id = second";
        String resultedQuery = QueryParametersBinder.bindParameters(NAMED_PARAMS_QUERY, "first", "second");
        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceBindParameters_with_wrong_number_of_params_should_throw_exception() {
        QueryParametersBinder.bindParameters(BIND_QUERY, "first","second","third","forth");
    }
}
