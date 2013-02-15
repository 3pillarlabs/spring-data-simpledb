package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.util.Assert;

public class CollectionExecution extends SimpleDbQueryExecution {

    public CollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        final Class<?> returnedClass = repositoryQuery.getQueryMethod().getReturnedObjectType();
        final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
        Assert.isAssignable(domainClass, returnedClass);
        return queryRunner.executeQuery();
    }
}
