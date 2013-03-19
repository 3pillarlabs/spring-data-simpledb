package org.springframework.data.simpledb.query.executions;

import org.springframework.data.domain.Page;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;

import java.util.List;

/**
 * Execute a paged query. A paged query may have as a result a {@link Page} or a {@link List}, depending on the method's
 * signature in the repository.
 */
public class PagedResultExecution extends AbstractSimpleDbQueryExecution {

	public PagedResultExecution(SimpleDbOperations simpleDbOperations) {
		super(simpleDbOperations);
	}

	@Override
	protected Object doExecute(SimpleDbQueryMethod queryMethod, SimpleDbQueryRunner queryRunner) {
		final Page<?> pagedResult = queryRunner.executePagedQuery();

		if(queryMethod.isPageQuery()) {
			return pagedResult;
		}

		return pagedResult.getContent();
	}

}
