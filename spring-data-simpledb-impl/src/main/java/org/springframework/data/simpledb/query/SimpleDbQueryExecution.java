package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.QueryUtils;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class SimpleDbQueryExecution {

    public SimpleDbQueryExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
        this.simpledbOperations = simpleDbOperations;
    }
    private final SimpleDbOperations<?, Serializable> simpledbOperations;

    public SimpleDbOperations<?, Serializable> getSimpledbOperations() {
        return simpledbOperations;
    }

    public Object execute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {

        Assert.notNull(repositoryQuery);
        Assert.notNull(values);

        SimpleDbQueryRunner queryRunner = new SimpleDbQueryRunner(simpledbOperations, repositoryQuery, values);
        return doExecute(repositoryQuery, values, queryRunner);
    }

    protected abstract Object doExecute(SimpleDbRepositoryQuery query, Object[] values, SimpleDbQueryRunner queryRunner);


    static class CountExecution extends SimpleDbQueryExecution {

        public CountExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            Class<?> methodReturnedType = repositoryQuery.getQueryMethod().getReturnedObjectType();
            boolean isLongClass = Long.class.isAssignableFrom(methodReturnedType);
            boolean islongClass = long.class.isAssignableFrom(methodReturnedType);
            Assert.isTrue(isLongClass || islongClass, "Method declared in repository should return type long or Long");
            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            return getSimpledbOperations().count(queryWithFilledParameters, consistentRead);
        }
    }

    static class CollectionExecution extends SimpleDbQueryExecution {

        public CollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] parameterValues, SimpleDbQueryRunner queryRunner) {
            final Class<?> returnedClass = repositoryQuery.getQueryMethod().getReturnedObjectType();
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            Assert.isAssignable(domainClass, returnedClass);

            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(parameterValues));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            return getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);
        }
    }

    static class PartialCollectionExecution extends SimpleDbQueryExecution {

        public PartialCollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            List<?> returnList = queryRunner.extractEntitiesFromDb();

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

    static class PartialCollectionFieldExecution extends SimpleDbQueryExecution {

        public PartialCollectionFieldExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            Object returnedEntity = queryRunner.extractSingleEntityFromDb();
            String attributeName = queryRunner.getSingleQueryFieldName();
            return ReflectionUtils.callGetter(returnedEntity, attributeName);
        }
    }

    static class PartialListOfOneFiledExecution extends SimpleDbQueryExecution {

        public PartialListOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            List<?> returnListFromDb = queryRunner.extractEntitiesFromDb();
            String attributeName = queryRunner.getSingleQueryFieldName();

            List<Object> returnList = new ArrayList<>();
            for (Object object : returnListFromDb) {
                returnList.add(ReflectionUtils.callGetter(object, attributeName));
            }
            return returnList;
        }
    }

    static class PartialSetOfOneFiledExecution extends SimpleDbQueryExecution {

        public PartialSetOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            List<?> returnListFromDb = queryRunner.extractEntitiesFromDb();
            String attributeName = queryRunner.getSingleQueryFieldName();
            Set<Object> returnList = new HashSet<>();
            for (Object object : returnListFromDb) {
                returnList.add(ReflectionUtils.callGetter(object, attributeName));
            }
            return returnList;
        }
    }

    static class SingleResultExecution extends SimpleDbQueryExecution {

        public SingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            return queryRunner.extractSingleEntityFromDb();
        }
    }

    static class PartialSingleResultExecution extends SimpleDbQueryExecution {

        public PartialSingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values, SimpleDbQueryRunner queryRunner) {
            Object returnedEntity = queryRunner.extractSingleEntityFromDb();
            String attributeName = queryRunner.getSingleQueryFieldName();
            return ReflectionUtils.callGetter(returnedEntity, attributeName);
        }
    }
}
