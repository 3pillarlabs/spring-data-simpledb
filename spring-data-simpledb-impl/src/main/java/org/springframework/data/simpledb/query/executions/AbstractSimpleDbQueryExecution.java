package org.springframework.data.simpledb.query.executions;

import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a
 * {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class AbstractSimpleDbQueryExecution {

	private final SimpleDbOperations simpledbOperations;

	public AbstractSimpleDbQueryExecution(SimpleDbOperations simpleDbOperations) {
		this.simpledbOperations = simpleDbOperations;
	}

	public Object execute(SimpleDbQueryMethod queryMethod, Object[] parameterValues) {
		Assert.notNull(queryMethod);
		Assert.notNull(parameterValues);

		// Demeter's Law
		QueryUtils.validateBindParametersCount(queryMethod.getParameters(), parameterValues);
		QueryUtils.validateBindParametersTypes(queryMethod.getParameters());

		Class<?> domainClass = queryMethod.getDomainClazz();
		String query = QueryUtils.bindQueryParameters(queryMethod, parameterValues);

		SimpleDbQueryRunner queryRunner;

		if(queryMethod.isPagedQuery()) {
			final Pageable pageable = getPageableParamValue(parameterValues);

			queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query, pageable);
		} else {
			queryRunner = new SimpleDbQueryRunner(simpledbOperations, domainClass, query);
		}

		return doExecute(queryMethod, queryRunner);
	}

	private Pageable getPageableParamValue(Object[] values) {
		Pageable pageable = null;

		for(Object value : values) {
			if(Pageable.class.isAssignableFrom(value.getClass())) {
				pageable = (Pageable) value;
			}
		}

		return pageable;
	}

	protected abstract Object doExecute(SimpleDbQueryMethod queryMethod, SimpleDbQueryRunner queryRunner);

}
