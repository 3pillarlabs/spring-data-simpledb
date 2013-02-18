package org.springframework.data.simpledb.query;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.util.Assert;

/**
 * This class is used to get information about query field names and execute queries
 */
public class SimpleDbQueryRunner {

    private final SimpleDbOperations<?, Serializable> simpledbOperations;
    private final Class<?> domainClass;
    private final String query;

    public SimpleDbQueryRunner(SimpleDbOperations<?, Serializable> simpledbOperations, Class<?> domainClass, String query) {
        this.simpledbOperations = simpledbOperations;
        this.domainClass = domainClass;
        this.query = query;
    }

    public List<?> executeQuery() {
        SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
        final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
        return simpledbOperations.find(entityInformation, query, consistentRead);
    }

    public Object executeSingleResultQuery() {
        List<?> returnListFromDb = executeQuery();
        Assert.isTrue(returnListFromDb.size() == 1, "Select statement doesn't return only one entity :" + query);
        return returnListFromDb.get(0);
    }

    public long executeCount() {
        final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
        return simpledbOperations.count(query, consistentRead);
    }

    public List<String> getRequestedQueryFieldNames() {
        return QueryUtils.getQueryPartialFieldNames(query);
    }

    public String getSingleQueryFieldName() {
        List<String> queryFieldNames = getRequestedQueryFieldNames();
        Assert.isTrue(queryFieldNames.size() == 1);
        return queryFieldNames.get(0);
    }
}
