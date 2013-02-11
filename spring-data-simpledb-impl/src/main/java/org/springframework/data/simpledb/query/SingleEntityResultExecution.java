package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.QueryParametersBinder;
import org.springframework.data.simpledb.util.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Executes the {@link org.springframework.data.simpledb.query.SimpleDbRepositoryQuery} to return a single entities.
 */
class SingleEntityResultExecution extends SimpleDbQueryExecution {

    public SingleEntityResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        super(simpleDbOperations);
    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
        final Class<?> domainClass = repositoryQuery.getQueryMethod().getReturnedObjectType();
        SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
        String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
        final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
        List<?> returnList = simpledbOperations.find(entityInformation, queryWithFilledParameters, consistentRead);
        return returnList.size() > 0 ? returnList.get(0) : null;
    }
}
