package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.ReflectionUtils;

public class PartialSetOfOneFiledExecution extends SimpleDbQueryExecution {

    public PartialSetOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        String attributeName = queryRunner.getSingleQueryFieldName();
        List<?> returnListFromDb = queryRunner.executeQuery();

        Set<Object> returnList = new HashSet<>();
        for (Object object : returnListFromDb) {
            returnList.add(ReflectionUtils.callGetter(object, attributeName));
        }
        return returnList;
    }
}