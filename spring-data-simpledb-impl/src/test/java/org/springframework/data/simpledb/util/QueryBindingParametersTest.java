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

    @Test
    public void bindNamedParameters_should_work_with_WHERE_clause() throws NoSuchMethodException {
        final String expectedQuery = "select * from spring_data where type = 'spring-type' ";

        FooRepositoryQuery repoQuery = new FooRepositoryQuery("fetchDataWithSimpleClause", String.class);
        String resultedQuery = QueryUtils.bindNamedParameters(repoQuery, "spring-type");

        assertThat(resultedQuery, is(expectedQuery));
    }


    @Test
    public void bindNamedParameters_should_return_a_formatted_query_back_to_caller() throws NoSuchMethodException {
        final String expectedQuery = "select * from spring_data where name = 'spring-name' and type = 'spring-type' or location = 'Timisoara' ";

        FooRepositoryQuery repoQuery = new FooRepositoryQuery("fetchDataWithMultipleClauses", String.class, String.class, String.class);
        String resultedQuery = QueryUtils.bindNamedParameters(repoQuery, "spring-type", "spring-name", "Timisoara");

        assertThat(resultedQuery, is(expectedQuery));
    }

    // TODO
    @Test
    public void bindQueryParameters_should_fail_if_wrong_number_of_parameters_and_values() {

    }

    // ------------------- SimpleDB Query Repositories which Mocks the current Implementation --------------------- //
    public interface NamedQueryRepository extends Repository {

        @Query("select * from spring_data where name = :name and type = :type or location = :location")
        public void fetchDataWithMultipleClauses(@Param(value="type") String dataType, @Param(value="name") String dataName, @Param(value = "location") String location);

        @Query("select * from spring_data where type = :type")
        public void fetchDataWithSimpleClause(@Param(value="type") String dataType);

    }

    public static class FooRepositoryQuery extends SimpleDbRepositoryQuery {
        private final static RepositoryMetadata metadata = new DefaultRepositoryMetadata(NamedQueryRepository.class);
        private final Method customSelectMethod;

        public FooRepositoryQuery(String methodName, Class<?>... parameterTypes) {
            this(getSimpleDbQueryMethod(methodName, parameterTypes), null, methodName, parameterTypes);

        }

        public FooRepositoryQuery(SimpleDbQueryMethod method, SimpleDbOperations<?, Serializable> simpledbOperations, String methodName, Class<?>... parameterTypes) {
            super(method, simpledbOperations);

            this.customSelectMethod = getMethod(methodName, parameterTypes);
        }

        private static SimpleDbQueryMethod getSimpleDbQueryMethod(String methodName, Class<?>... parameterTypes) {
            return new SimpleDbQueryMethod(getMethod(methodName, parameterTypes), metadata);
        }

        private static Method getMethod(String methodName, Class<?>... parameterTypes) {
            Method method = null;
            try {
                method = NamedQueryRepository.class.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return method;
        }

        @Override
        public Object execute(Object[] parameters) {
            return null;
        }

        @Override
        public QueryMethod getQueryMethod() {
            RepositoryMetadata metadata = new DefaultRepositoryMetadata(NamedQueryRepository.class);
            return new QueryMethod(customSelectMethod, metadata);
        }

        @Override
        public String getAnnotatedQuery() {
            return new SimpleDbQueryMethod(customSelectMethod, metadata).getAnnotatedQuery();
        }
    }
}
