package org.springframework.data.simpledb.query.executions;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.query.SimpleDbResultConverter;

import java.io.Serializable;
import java.util.List;

public class PartialCollectionExecution extends AbstractSimpleDbQueryExecution {

    public PartialCollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        List<?> returnList = queryRunner.executeQuery();
        List<String> requestedQueryFieldNames = queryRunner.getRequestedQueryFieldNames();
        return SimpleDbResultConverter.toListBasedRepresentation(returnList, requestedQueryFieldNames);
    }

}
