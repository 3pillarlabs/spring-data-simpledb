package org.springframework.data.simpledb.query;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cclaudiu
 */
public class QueryUtilsIndexByQueryTest {

    private static final String BIND_QUERY = "select * from customer_all WHERE customer_id = ? and email_id = ? and id = ?";

    @Test
    public void replaceBindParameters_should_construct_correct_query_from_bind_parameters() {
        String expectedQuery = "select * from customer_all WHERE customer_id = 'first' and email_id = 'second' and id = 'third'";
        String resultedQuery = QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third");

        assertThat(resultedQuery, is(expectedQuery));
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceBindParameters_with_wrong_number_of_params_should_throw_exception() {
        QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third", "forth");
    }
}
