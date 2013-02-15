package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.util.Assert;

public class CountExecution extends SimpleDbQueryExecution {

    public CountExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        Class<?> methodReturnedType = repositoryQuery.getQueryMethod().getReturnedObjectType();
        boolean isLongClass = Long.class.isAssignableFrom(methodReturnedType);
        boolean islongClass = long.class.isAssignableFrom(methodReturnedType);
        Assert.isTrue(isLongClass || islongClass, "Method declared in repository should return type long or Long");
        return queryRunner.executeCount();
    }
}
