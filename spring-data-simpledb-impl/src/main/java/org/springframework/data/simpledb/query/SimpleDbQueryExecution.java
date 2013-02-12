package org.springframework.data.simpledb.query;

import java.io.Serializable;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class SimpleDbQueryExecution {

    public SimpleDbQueryExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        this.simpledbOperations = simpleDbOperations;
    }
    final protected SimpleDbOperations<?, Serializable> simpledbOperations;

    public Object execute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {

        Assert.notNull(repositoryQuery);
        Assert.notNull(values);

        return doExecute(repositoryQuery, values);
    }

    protected abstract Object doExecute(SimpleDbRepositoryQuery query, Object[] values);

    /**
     * Executes the {@link SimpleDbRepositoryQuery} to return a simple collection of entities.
     */
    static class CollectionExecution extends SimpleDbQueryExecution {

        public CollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] parameterValues) {
            final Class<?> domainClass = repositoryQuery.getQueryMethod().getReturnedObjectType();

            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);

            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(parameterValues));

            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();

            return simpledbOperations.find(entityInformation, queryWithFilledParameters, consistentRead);
        }
    }
}
