package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class AbstractSimpleDbQueryExecution {

	private final SimpleDbOperations<?, Serializable> simpledbOperations;
	
    public AbstractSimpleDbQueryExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        this.simpledbOperations = simpleDbOperations;
    }

    public Object execute(SimpleDbRepositoryQuery repositoryQuery, Object[] parameterValues) {
        Assert.notNull(repositoryQuery);
        Assert.notNull(parameterValues);
        
        // Demeter's Law
		QueryUtils.validateBindParametersCount(repositoryQuery.getQueryParameters(), parameterValues);
		QueryUtils.validateBindParametersTypes(repositoryQuery.getQueryParameters(), parameterValues);
		
        SimpleDbQueryMethod queryMethod = (SimpleDbQueryMethod)repositoryQuery.getQueryMethod();
		Class<?> domainClass = queryMethod.getDomainClazz();
        String query = QueryUtils.bindQueryParameters(repositoryQuery, domainClass, parameterValues);
        
		SimpleDbQueryRunner queryRunner;
		
		if(queryMethod.isPagedQuery()) {
			final Pageable pageable = getPageableParamValue(parameterValues);
			
			queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query, pageable);
		} else {
			queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query);			
		}
        
        return doExecute(repositoryQuery, queryRunner);
    }
    
	private Pageable getPageableParamValue(Object[] values) {
		Pageable pageable = null;
		
		for(Object value: values) {
			if(Pageable.class.isAssignableFrom(value.getClass())) {
				pageable = (Pageable)value;
			}
		}
		
		return pageable;
	}
    
    protected abstract Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner);
    
}
