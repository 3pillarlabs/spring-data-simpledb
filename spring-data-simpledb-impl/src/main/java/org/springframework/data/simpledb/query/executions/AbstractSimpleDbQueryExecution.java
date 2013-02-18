package org.springframework.data.simpledb.query.executions;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class AbstractSimpleDbQueryExecution {

	private final SimpleDbOperations<?, Serializable> simpledbOperations;
	
    public AbstractSimpleDbQueryExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        this.simpledbOperations = simpleDbOperations;
    }

    public Object execute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
        Assert.notNull(repositoryQuery);
        Assert.notNull(values);
        String query = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
        Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClazz();
        SimpleDbQueryRunner queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query);
        return doExecute(repositoryQuery, queryRunner);
    }

    protected SimpleDbOperations<?, Serializable> getSimpledbOperations() {
		return simpledbOperations;
	}
    
    protected abstract Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner);
    
}
