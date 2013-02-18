package org.springframework.data.simpledb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.executions.*;
import org.springframework.data.simpledb.util.FieldType;
import org.springframework.data.simpledb.util.FieldTypeIdentifier;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * {@link RepositoryQuery} implementation that inspects a {@link SimpleDbQueryMethod} for the existence of an {@link org.springframework.data.simpledb.annotation.Query} annotation and provides
 * implementations based on query method information.
 */
public class SimpleDbRepositoryQuery implements RepositoryQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbRepositoryQuery.class);
    private final SimpleDbQueryMethod method;
    private final SimpleDbOperations<?, Serializable> simpledbOperations;

    public SimpleDbRepositoryQuery(SimpleDbQueryMethod method, SimpleDbOperations<?, Serializable> simpledbOperations) {
        this.method = method;
        this.simpledbOperations = simpledbOperations;
    }

    @Override
    public Object execute(Object[] parameters) {
        return getExecution().execute(this, parameters);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return method;
    }

    /**
     * Creates a {@link RepositoryQuery} from the given {@link org.springframework.data.repository.query.QueryMethod} that is potentially annotated with
     * {@link org.springframework.data.simpledb.annotation.Query}.
     *
     * @param queryMethod
     * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
     */
    public static RepositoryQuery fromQueryAnnotation(SimpleDbQueryMethod queryMethod, SimpleDbOperations<?, Serializable> simpleDbOperations) {
        LOGGER.debug("Looking up query for method {}", queryMethod.getName());
        return queryMethod.getAnnotatedQuery() == null ? null : new SimpleDbRepositoryQuery(queryMethod, simpleDbOperations);
    }

    public String getAnnotatedQuery() {
        return method.getAnnotatedQuery();
    }

    protected AbstractSimpleDbQueryExecution getExecution() {
        String query = method.getAnnotatedQuery();
        assertNotHavingNestedQueryParameters(query);
        if(method.isCollectionQuery()){
            return new MultipleResultExecution(simpledbOperations);
        } else if (method.isModifyingQuery()){
            throw new IllegalArgumentException("Not implemented");
        } else if (method.isPageQuery()){
            throw new IllegalArgumentException("Not implemented");
        }
//          else {
//            return new SingleResultExecution(simpledbOperations);
//        }

        if (QueryUtils.isCountQuery(query)) {
            return new CountExecution(simpledbOperations);
        } else if (method.isQueryForEntity()) {
            return new SingleResultExecution(simpledbOperations);
        } else if (method.isPageQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else if (method.isModifyingQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else {
            return new PartialSingleResultExecution(simpledbOperations);
        }
    }

    private void assertNotHavingNestedQueryParameters(String query) {
        List<String> attributesFromQuery = QueryUtils.getQueryPartialFieldNames(query);
        final Class<?> domainClass = method.getDomainClass();
        for (String attribute : attributesFromQuery) {
            try {
                Field field = domainClass.getDeclaredField(attribute);
                if (FieldTypeIdentifier.isOfType(field, FieldType.NESTED_ENTITY)) {
                    throw new IllegalArgumentException("Invalid query parameter :" + attribute + " is nested object");
                }
            } catch (NoSuchFieldException e) {
                //might be a count or something else
            }
        }
    }
}
