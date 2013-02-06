package org.springframework.data.simpledb.query;


import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a
 * {@link org.springframework.data.repository.query.QueryMethod}
 */
public abstract class SimpleDbQueryExecution {

    public Object execute(SimpleDbQuery query, Object[] values) {

        Assert.notNull(query);
        Assert.notNull(values);

        return doExecute(query, values);
    }

    protected abstract Object doExecute(SimpleDbQuery query, Object[] values);

    /**
     * Executes the {@link SimpleDbQuery} to return a simple collection of entities.
     */
    static class CollectionExecution extends SimpleDbQueryExecution {

        @Override
        protected Object doExecute(SimpleDbQuery query, Object[] values) {
            //TODO add colection impementation here
            return null;
        }
    }

    //TODO add other executions




}
