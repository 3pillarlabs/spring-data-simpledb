package org.springframework.data.simpledb.util;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbOperationsImpl;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
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
    @Test public void bindNamedParameters_should_return_a_formatted_query_back_to_caller() throws NoSuchMethodException {
        final String expectedQuery = "SELECT * FROM customer_all where customer_id = spring and email_id = data";
        RepositoryMetadata metadata = new DefaultRepositoryMetadata(Foo.class);
        SimpleDbQueryMethod method = new SimpleDbQueryMethod(Foo.class.getDeclaredMethod("fetchData", String.class, String.class), metadata);
        SimpleDbOperations ops = new SimpleDbOperationsImpl(null);
        SimpleDbRepositoryQuery query = new SimpleDbRepositoryQuery(method, null);
        String resultedQuery = QueryUtils.bindNamedParameters(null, "spring", "data");

        assertThat(resultedQuery, is(expectedQuery));
    }

    public interface Foo {
        @Query("SELECT * FROM spring_data WHERE name = :name AND type = :type")
        void fetchData(@Param(value="spring") String springValue, @Param(value="type") String dataValue);
    }
}
