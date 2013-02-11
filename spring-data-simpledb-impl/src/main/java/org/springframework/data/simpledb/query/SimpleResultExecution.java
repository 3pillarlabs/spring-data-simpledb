package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.util.QueryParametersBinder;
import org.springframework.data.simpledb.util.StringUtil;

import java.io.Serializable;

/**
 * Executes the {@link org.springframework.data.simpledb.query.SimpleDbRepositoryQuery} to return a count value, a value of a field and so on.
 */
class SimpleResultExecution extends SimpleDbQueryExecution {

    public SimpleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
        if (repositoryQuery.getAnnotatedQuery().toLowerCase().contains("count(")) {
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            return simpledbOperations.count(queryWithFilledParameters, consistentRead);
        } else {
            return null;
        }
    }
}
