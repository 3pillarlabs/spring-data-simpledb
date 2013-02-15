package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;

public class SingleResultExecution extends AbstractSimpleDbQueryExecution {

    public SingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        return queryRunner.executeSingleResultQuery();
    }
}