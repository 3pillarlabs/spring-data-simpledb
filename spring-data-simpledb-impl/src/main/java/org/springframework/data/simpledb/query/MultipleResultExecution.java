package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.executions.AbstractSimpleDbQueryExecution;

import java.io.Serializable;

/**
 * Created by: mgrozea
 */
public class MultipleResultExecution extends AbstractSimpleDbQueryExecution {

	public MultipleResultExecution(SimpleDbOperations<?, Serializable> simpledbOperations) {
        super(simpledbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner) {
    	/* To change body of implemented methods use File | Settings | File Templates. */
        return null;
    }
}
