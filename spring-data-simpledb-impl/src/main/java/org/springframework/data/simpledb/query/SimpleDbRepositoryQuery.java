package org.springframework.data.simpledb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryExecution.CollectionExecution;
import org.springframework.data.simpledb.query.SimpleDbQueryExecution.CountExecution;

import java.io.Serializable;
import org.springframework.data.simpledb.query.SimpleDbQueryExecution.SingleResultExecution;

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

    public String getAnnotatedQuery() {
        return method.getAnnotatedQuery();
    }

    protected SimpleDbQueryExecution getExecution() {
        //TODO this is shit and needs fixing
        if (method.isCollectionQuery()) {
            return new CollectionExecution(simpledbOperations);
        } else if (method.isPageQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else if (method.isModifyingQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else if (method.getAnnotatedQuery().toLowerCase().contains("count(")) {
            return new CountExecution(simpledbOperations);
        } else if (method.isQueryForEntity()) {
            return new SingleResultExecution(simpledbOperations);
        } else {
            throw new IllegalArgumentException("Provided query not recognized by simpleDB: " + method.getAnnotatedQuery());
        }
        //TODO isPageQuery?
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
}
