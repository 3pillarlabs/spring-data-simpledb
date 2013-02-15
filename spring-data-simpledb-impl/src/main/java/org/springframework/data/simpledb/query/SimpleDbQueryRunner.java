package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.QueryUtils;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to get information about query field names and execute queries
 */
public class SimpleDbQueryRunner {
    SimpleDbOperations<?, Serializable> simpledbOperations;
    SimpleDbRepositoryQuery repositoryQuery;
    Object[] parameterValues;

    public SimpleDbQueryRunner(SimpleDbOperations<?, Serializable> simpledbOperations, SimpleDbRepositoryQuery repositoryQuery, Object[] parameterValues) {
        this.simpledbOperations = simpledbOperations;
        this.repositoryQuery = repositoryQuery;
        this.parameterValues = parameterValues;
    }

    public List<?> extractEntitiesFromDb() {
        final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
        SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
        String queryWithFilledParameters = getQueryWithFilledParameters();
        final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();

        return simpledbOperations.find(entityInformation, queryWithFilledParameters, consistentRead);
    }

    public Object extractSingleEntityFromDb(){
        List<?> returnListFromDb = extractEntitiesFromDb();
        Assert.isTrue(returnListFromDb.size() == 1, "Select statement doesn't return only one entity :" + repositoryQuery.getAnnotatedQuery());
        return returnListFromDb.get(0);
    }

    public List<String> getRequestedQueryFieldNames() {
        String queryWithFilledParameters = getQueryWithFilledParameters();
        return QueryUtils.getQueryPartialFieldNames(queryWithFilledParameters);
    }

    public String getSingleQueryFieldName(){
        List<String> queryFieldNames = getRequestedQueryFieldNames();
        Assert.isTrue(queryFieldNames.size()==1);
        return queryFieldNames.get(0);
    }

    private String getQueryWithFilledParameters() {
        return QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(parameterValues));
    }

}
