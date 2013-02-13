package org.springframework.data.simpledb.query;

import java.beans.IntrospectionException;
import org.springframework.data.simpledb.util.QueryUtils;
import java.beans.PropertyDescriptor;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.util.ReflectionUtils;

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

        return doExecute(repositoryQuery, values);
    }

    protected abstract Object doExecute(SimpleDbRepositoryQuery query, Object[] values);

    static class CountExecution extends SimpleDbQueryExecution {

        public CountExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            boolean isLongClass = repositoryQuery.getQueryMethod().getReturnedObjectType().isAssignableFrom(Long.class);
            boolean islongClass = repositoryQuery.getQueryMethod().getReturnedObjectType().isAssignableFrom(long.class);
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
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] parameterValues) {
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
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();

            List<?> returnList = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);

            List<String> requestedQueryFieldNames = QueryUtils.getQueryPartialFieldNames(queryWithFilledParameters);
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

        private Object getFieldValue(Object obj, String fieldName) {
            try {
                Method method = new PropertyDescriptor(fieldName, obj.getClass()).getReadMethod();
                return method.invoke(obj);

            } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new MappingException("Could not retrieve field value for Field=" + fieldName, e);
            }
        }
    }

    static class SingleResultExecution extends SimpleDbQueryExecution {

        public SingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnList = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);
            Assert.isTrue(returnList.size() == 1, "Select statement doesn't return only one entity :" + repositoryQuery.getAnnotatedQuery());
            return returnList.get(0);
        }
    }

    static class PartialSingleResultExecution extends SimpleDbQueryExecution {

        public PartialSingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryUtils.bindQueryParameters(repositoryQuery, StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnList = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);
            //TODO serialize to which is expected
            return null;
        }
    }
}
