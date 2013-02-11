package org.springframework.data.simpledb.util;

import org.junit.Test;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;

import java.io.Serializable;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryBindingParametersTest {

    private static final String BIND_QUERY = "SELECT * FROM customer_all where customer_id = ? and email_id = ? and id = ?";

    @Test
    public void replaceBindParameters_should_construct_correct_query_from_bind_parameters() {
        String expectedQuery = "SELECT * FROM customer_all where customer_id = 'first' and email_id = 'second' and id = 'third'";
        String resultedQuery = QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third");

        assertThat(resultedQuery, is(expectedQuery));
    }


    @Test(expected = IllegalArgumentException.class)
    public void replaceBindParameters_with_wrong_number_of_params_should_throw_exception() {
        QueryUtils.bindIndexPositionParameters(BIND_QUERY, "first", "second", "third", "forth");
    }

    @Test public void bindNamedParameters_should_return_a_formatted_query_back_to_caller() throws NoSuchMethodException {
        final String expectedQuery = "SELECT * FROM SPRING_DATA WHERE TYPE = 'spring-type' AND NAME = 'spring-name'";

        FooRepositoryQuery repoQuery = new FooRepositoryQuery();
        String resultedQuery = QueryUtils.bindNamedParameters(repoQuery, "spring-type", "spring-name");

        assertThat(resultedQuery, is(expectedQuery));
    }

    // ------------------- SimpleDB Query Repositories which Mock the current Implementation --------------------- //
    public interface FooRepository extends Repository {

        @Query("SELECT * FROM spring_data WHERE name = :name AND type = :type")
        void fetchData(@Param(value="type") String dataType, @Param(value="name") String dataName);

    }

    public static class FooRepositoryQuery extends SimpleDbRepositoryQuery {
        Method fetchDataMethod = null;
        RepositoryMetadata metadata = null;

        public FooRepositoryQuery() {
            this(null, null);

            try {
                fetchDataMethod = FooRepository.class.getDeclaredMethod("fetchData", String.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            metadata = new DefaultRepositoryMetadata(FooRepository.class);
        }

        public FooRepositoryQuery(SimpleDbQueryMethod method, SimpleDbOperations<?, Serializable> simpledbOperations) {
            super(method, simpledbOperations);
        }

        @Override
        public Object execute(Object[] parameters) {
            return null;
        }

        @Override
        public QueryMethod getQueryMethod() {
            RepositoryMetadata metadata = new DefaultRepositoryMetadata(FooRepository.class);
            return new QueryMethod(fetchDataMethod, metadata);
        }

        @Override
        public String getAnnotatedQuery() {
            return new SimpleDbQueryMethod(fetchDataMethod, metadata).getAnnotatedQuery();
        }
    }
}
