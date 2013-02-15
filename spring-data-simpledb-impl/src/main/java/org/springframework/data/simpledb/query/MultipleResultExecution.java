package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.executions.AbstractSimpleDbQueryExecution;

import java.io.Serializable;

/**
 * Created by: mgrozea
 */
public class MultipleResultExecution extends AbstractSimpleDbQueryExecution {
    private SimpleDbOperations<?, Serializable> simpledbOperations;

    public MultipleResultExecution(SimpleDbOperations<?, Serializable> simpledbOperations) {
        super(simpledbOperations);
        this.simpledbOperations = simpledbOperations;
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
