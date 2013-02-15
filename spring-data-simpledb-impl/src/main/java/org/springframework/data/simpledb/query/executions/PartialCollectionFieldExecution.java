package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.ReflectionUtils;

public class PartialCollectionFieldExecution extends AbstractSimpleDbQueryExecution {

    public PartialCollectionFieldExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        String attributeName = queryRunner.getSingleQueryFieldName();
        Object returnedEntity = queryRunner.executeSingleResultQuery();
        return ReflectionUtils.callGetter(returnedEntity, attributeName);
    }
}