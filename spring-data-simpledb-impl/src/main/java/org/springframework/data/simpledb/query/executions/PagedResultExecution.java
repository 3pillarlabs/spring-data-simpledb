package org.springframework.data.simpledb.query.executions;

import org.springframework.data.domain.Page;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;

import java.io.Serializable;


/**
 * Execute a paged query. A paged query may have as a result a {@link Page} or a {@link List},
 * depending on the method's signature in the repository.
 */
public class PagedResultExecution extends AbstractSimpleDbQueryExecution {

    public PagedResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner) {
        final Page<?> pagedResult = queryRunner.executePagedQuery();

        if (query.getQueryMethod().isPageQuery()) {
            return pagedResult;
        }

        return pagedResult.getContent();
    }

}
