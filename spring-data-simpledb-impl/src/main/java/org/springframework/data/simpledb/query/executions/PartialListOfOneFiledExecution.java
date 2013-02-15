package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.ReflectionUtils;

public class PartialListOfOneFiledExecution extends SimpleDbQueryExecution {

    public PartialListOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        String attributeName = queryRunner.getSingleQueryFieldName();
        List<?> returnListFromDb = queryRunner.executeQuery();

        List<Object> returnList = new ArrayList<>();
        for (Object object : returnListFromDb) {
            returnList.add(ReflectionUtils.callGetter(object, attributeName));
        }
        return returnList;
    }
}