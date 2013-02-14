package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbMetamodelEntityInformation;
import org.springframework.data.simpledb.util.QueryParametersBinder;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
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

    public SimpleDbOperations<?, Serializable> getSimpledbOperations(){
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
            Class<?> methodReturnedType = repositoryQuery.getQueryMethod().getReturnedObjectType();
            boolean isLongClass = Long.class.isAssignableFrom(methodReturnedType);
            boolean islongClass = long.class.isAssignableFrom(methodReturnedType);
            Assert.isTrue(isLongClass || islongClass, "Method declared in repository should return type long or Long");
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            return getSimpledbOperations().count(queryWithFilledParameters, consistentRead);
        }
    }

    static class CollectionExecution extends SimpleDbQueryExecution {

        public CollectionExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> returnedClass = repositoryQuery.getQueryMethod().getReturnedObjectType();
            final Class<?> domainClass = ((SimpleDbQueryMethod)repositoryQuery.getQueryMethod()).getDomainClass();
            Assert.isAssignable(domainClass, returnedClass);

            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
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
            return null;
        }
    }


    static class PartialCollectionFieldExecution extends SimpleDbQueryExecution {

        public PartialCollectionFieldExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnListFromDb = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);

            String query = repositoryQuery.getAnnotatedQuery();
            List<String> attributesFromQuery = StringUtil.getAttributesInQuerry(query);
            String attributeName = attributesFromQuery.get(0);
            try {
                Field field = domainClass.getDeclaredField(attributeName);
                field.setAccessible(true);
                Assert.isTrue(returnListFromDb.size() == 1, "Select statement doesn't return only one entity :" + repositoryQuery.getAnnotatedQuery());
                return field.get(returnListFromDb.get(0));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Filed doesn't exist in entity :" + query, e);
            }
        }
    }

    static class PartialListOfOneFiledExecution extends SimpleDbQueryExecution {

        public PartialListOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnListFromDb = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);

            String query = repositoryQuery.getAnnotatedQuery();
            List<String> attributesFromQuery = StringUtil.getAttributesInQuerry(query);
            String attributeName = attributesFromQuery.get(0);
            try {
                Field field = domainClass.getDeclaredField(attributeName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                List<Object> returnList = new ArrayList<>();
                for (Object object : returnListFromDb) {
                    returnList.add(field.get(object));
                }
                return returnList;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Filed doesn't exist in entity :" + query);
            }
        }
    }

    static class PartialSetOfOneFiledExecution extends SimpleDbQueryExecution {

        public PartialSetOfOneFiledExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod) repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnListFromDb = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);

            String query = repositoryQuery.getAnnotatedQuery();
            List<String> attributesFromQuery = StringUtil.getAttributesInQuerry(query);
            String attributeName = attributesFromQuery.get(0);
            try {
                Field field = domainClass.getDeclaredField(attributeName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                Set<Object> returnList = new HashSet<>();
                for (Object object : returnListFromDb) {
                    returnList.add(field.get(object));
                }
                return returnList;

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Filed doesn't exist in entity :" + query);
            }
        }
    }

    static class SingleResultExecution extends SimpleDbQueryExecution {

        public SingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod)repositoryQuery.getQueryMethod()).getReturnedObjectType();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnList = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);
            Assert.isTrue(returnList.size()==1, "Select statement doesn't return only one entity :"+repositoryQuery.getAnnotatedQuery());
            return returnList.get(0);
        }
    }

    static class PartialSingleResultExecution extends SimpleDbQueryExecution {

        public PartialSingleResultExecution(SimpleDbOperations<?, Serializable> simpleDbOperations) {
            super(simpleDbOperations);
        }

        @Override
        protected Object doExecute(SimpleDbRepositoryQuery repositoryQuery, Object[] values) {
            final Class<?> domainClass = ((SimpleDbQueryMethod)repositoryQuery.getQueryMethod()).getDomainClass();
            SimpleDbEntityInformation entityInformation = new SimpleDbMetamodelEntityInformation(domainClass);
            String queryWithFilledParameters = QueryParametersBinder.bindParameters(repositoryQuery.getAnnotatedQuery(), StringUtil.toStringArray(values));
            final boolean consistentRead = SimpleDbConfig.getInstance().isConsistentRead();
            List<?> returnList = getSimpledbOperations().find(entityInformation, queryWithFilledParameters, consistentRead);

            Assert.isTrue(returnList.size()==1, "Select statement doesn't return only one entity :"+repositoryQuery.getAnnotatedQuery());
            String query = repositoryQuery.getAnnotatedQuery();
            final Class<?> returnedClass = repositoryQuery.getQueryMethod().getReturnedObjectType();
            List<String> attributesFromQuery = StringUtil.getAttributesInQuerry(query);
            String attributeName = attributesFromQuery.get(0);
            try {
                Field field = domainClass.getDeclaredField(attributeName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                return field.get(returnList.get(0));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Filed doesn't exist in entity :" + query);
            }
        }
    }
}
