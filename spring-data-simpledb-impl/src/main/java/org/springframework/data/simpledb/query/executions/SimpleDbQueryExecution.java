package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.QueryUtils;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class SimpleDbQueryExecution {

    public SimpleDbQueryExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        this.simpledbOperations = simpleDbOperations;
    }
    private final SimpleDbOperations<?, Serializable> simpledbOperations;

    public Object execute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
        Assert.notNull(repositoryQuery);
        Assert.notNull(values);
        String query = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
        Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
        SimpleDbQueryRunner queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query);
        return doExecute(repositoryQuery, queryRunner);
    }

    protected abstract Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner);
}
