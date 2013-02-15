package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.util.ReflectionUtils;

public class PartialCollectionExecution extends SimpleDbQueryExecution {

    public PartialCollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, SimpleDbQueryRunner queryRunner) {
        List<?> returnList = queryRunner.executeQuery();

        List<String> requestedQueryFieldNames = queryRunner.getRequestedQueryFieldNames();
        return toListBasedRepresentation(returnList, requestedQueryFieldNames);
    }

    private Object toListBasedRepresentation(List<?> entityList, List<String> requestedQueryFieldNames) throws MappingException {
        if (entityList.size() > 0) {
            List<List<Object>> rows = new ArrayList<>();
            for (Object entity : entityList) {
                List<Object> cols = new ArrayList<>();
                for (String fieldName : requestedQueryFieldNames) {
                    Object value = ReflectionUtils.callGetter(entity, fieldName);
                    cols.add(value);
                }
                rows.add(cols);
            }
            return rows;
        } else {
            return null;
        }
    }
}
