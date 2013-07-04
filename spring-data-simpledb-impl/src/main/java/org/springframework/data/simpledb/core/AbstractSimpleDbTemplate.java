package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SdbItemQuery;
import org.springframework.data.simpledb.reflection.ReflectionUtils;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.SelectResult;

/**
 * Performs Domain Management based on Domain Management Policy on each operation
 */
public abstract class AbstractSimpleDbTemplate implements SimpleDbOperations {

    private final int serviceUnavailableMaxRetries;
    private final SimpleDb simpleDb;
    private final AmazonSimpleDB simpleDbClient;

    public AbstractSimpleDbTemplate(SimpleDb simpleDb) {
        Assert.notNull(simpleDb);
        this.simpleDb = simpleDb;
        this.simpleDbClient = simpleDb.getSimpleDbClient();
        this.serviceUnavailableMaxRetries = simpleDb.getUnavailableServiceRetries();
    }

    public abstract <T> Page<T> executePagedQueryImpl(Class<T> entityClass, String query, Pageable pageable,
                                                      boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract <T> T createOrUpdateImpl(T domainItem, EntityWrapper<T, ?> entity);

    public abstract void deleteAttributesImpl(String domainName, String itemName);

    public abstract <T> void deleteImpl(T domainItem, SimpleDbEntityInformation<T, ?> entityInformation, EntityWrapper<T, ?> entity);

    public abstract <T> List<T> findAllQueryImpl(Class<T> entityClass, SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract <T> List<T> recursiveFindImpl(Class<T> entityClass, String query, boolean consistentRead,
                                                  SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract <T> long countImpl(boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract SelectResult invokeFindImpl(boolean consistentRead, String escapedQuery);

    public abstract <T> long countImpl(String query, boolean consistentRead,
                                       SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract <T, ID extends Serializable> T readImpl(ID id, Class<T> entityClass, boolean consistentRead,
                                                            SimpleDbEntityInformation<T, ?> entityInformation);

    public abstract <T> List<T> findImpl(SimpleDbEntityInformation<T, ?> entityInformation, String query,
                                         String nextToken, boolean consistentRead);

    protected abstract <T, ID> void updateImpl(ID id, Class<T> entityClass, Map<String, Object> propertyMap);
    
    @Override
    public final AmazonSimpleDB getDB() {
        return simpleDb.getSimpleDbClient();
    }

    @Override
    public final SimpleDb getSimpleDb() {
        return simpleDb;
    }

    @Override
    public final String getDomainName(Class<?> entityClass) {
        return simpleDb.getDomain(entityClass);
    }

    @Override
    public final <T> T createOrUpdate(final T domainItem) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(domainItem.getClass());
        final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem, entityInformation);

        manageSimpleDbDomains(entityInformation);

        final List<T> items = new ArrayList<T>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.add(createOrUpdateImpl(domainItem, entity));
            }
        }.executeWithRetries();

        return items.size() > 0 ? items.get(0) : null;
    }

	@Override
    public final void delete(final String domainName, final String itemName) {
        manageSimpleDbDomain(domainName);

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                deleteAttributesImpl(domainName, itemName);
            }
        }.executeWithRetries();
    }

    @Override
    public final <T> void delete(final T domainItem) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(domainItem.getClass());
        final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem, entityInformation);

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                deleteImpl(domainItem, entityInformation, entity);
            }
        }.executeWithRetries();
    }

    @Override
	public <T, ID> void delete(Class<T> entityClass, ID id) {
    	delete(getDomainName(entityClass), (String) id);
	}

	@Override
	public <T> void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
    public final <T> void deleteAll(Class<T> entityClass) {
        for (T element : findAll(entityClass, true)) {
            delete(element);
        }
    }

    @Override
    public final <T, ID extends Serializable> T read(ID id, Class<T> entityClass) {
        return read(id, entityClass, simpleDb.isConsistentRead());
    }

    @Override
    public final <T, ID extends Serializable> T read(final ID id, final Class<T> entityClass,
                                                     final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        manageSimpleDbDomains(entityInformation);

        final List<T> items = new ArrayList<T>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.add(readImpl(id, entityClass, consistentRead, entityInformation));
            }
        }.executeWithRetries();

        return items.size() > 0 ? items.get(0) : null;
    }

    @Override
    public final <T> long count(Class<T> entityClass) {
        return count(entityClass, simpleDb.isConsistentRead());
    }

    @Override
    public final <T> long count(String query, Class<T> entityClass) {
        return count(query, entityClass, simpleDb.isConsistentRead());
    }

    @Override
    public final <T> long count(final String query, final Class<T> entityClass, final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        manageSimpleDbDomains(entityInformation);

        final List<Long> items = new ArrayList<Long>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.add(countImpl(query, consistentRead, entityInformation));
            }
        }.executeWithRetries();

        return items.size() > 0 ? items.get(0) : 0;
    }

    @Override
    public final <T> long count(final Class<T> entityClass, final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        manageSimpleDbDomains(entityInformation);

        final List<Long> items = new ArrayList<Long>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.add(countImpl(consistentRead, entityInformation));
            }
        }.executeWithRetries();

        return items.size() > 0 ? items.get(0) : 0;
    }

    @Override
    public final <T> List<T> find(Class<T> entityClass, String query) {
        return find(entityClass, query, simpleDb.isConsistentRead());
    }

    @Override
    public <T> List<T> find(final Class<T> entityClass, final String query, final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        manageSimpleDbDomains(entityInformation);

        final List<T> items = new ArrayList<T>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.addAll(recursiveFindImpl(entityClass, query, consistentRead, entityInformation));
            }
        }.executeWithRetries();

        return items;
    }

    protected <T> List<T> find(final SimpleDbEntityInformation<T, ?> entityInformation, final String query,
                               final String nextToken, final boolean consistentRead) {
        manageSimpleDbDomains(entityInformation);

        final List<T> items = new ArrayList<T>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.addAll(findImpl(entityInformation, query, nextToken, consistentRead));
            }
        }.executeWithRetries();

        return items;
    }

    @Override
    public final <T> List<T> findAll(Class<T> entityClass) {
        return findAll(entityClass, simpleDb.isConsistentRead());
    }

    @Override
    public final <T> List<T> findAll(final Class<T> entityClass, final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        manageSimpleDbDomains(entityInformation);

        final List<T> items = new ArrayList<T>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                items.addAll(findAllQueryImpl(entityClass, entityInformation));
            }
        }.executeWithRetries();

        return items;
    }

    @Override
    public final <T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable) {
        return executePagedQuery(entityClass, query, pageable, simpleDb.isConsistentRead());
    }

    @Override
    public final <T> Page<T> executePagedQuery(final Class<T> entityClass, final String query, final Pageable pageable,
                                               final boolean consistentRead) {
        final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

        final List<Page<T>> pages = new ArrayList<Page<T>>();

        new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {

            @Override
            public void execute() {
                pages.add(executePagedQueryImpl(entityClass, query, pageable, consistentRead, entityInformation));
            }
        }.executeWithRetries();

        return pages.isEmpty() ? null : pages.get(0);
    }

    @Override
	public <T, ID> void update(final ID id, final Class<T> entityClass, 
			final Map<String, Object> propertyMap) {

		new AbstractServiceUnavailableOperationRetrier(serviceUnavailableMaxRetries) {
			
			@Override
			public void execute() {
				updateImpl(id, entityClass, propertyMap);
			}
		}.executeWithRetries();
    }

	@Override
	public <T> SdbItemQuery<T> createQuery(Class<T> entityClass, String rawWhereClause, Object...queryParams) {
		
		String whereClause = rawWhereClause;
		
		// replace positional parameters
		final String paramPlaceholder = "\\?";
		for (int i = 0; QueryUtils.hasBindParameter(whereClause); i++) {
			whereClause = QueryUtils.replaceOneParameterInQuery(
					whereClause, paramPlaceholder, queryParams[i]);
		}
		
		// add select * from `domainName`
		String query = String.format("SELECT * FROM `%s` WHERE %s", 
				getDomainName(entityClass), whereClause);
		
		return new SdbItemQuery<T>(entityClass, query, this);
	}

	protected final <T> EntityWrapper<T, ?> getEntityWrapper(T domainItem,
                                                             SimpleDbEntityInformation<T, ?> entityInformation) {
        final EntityWrapper<T, ?> entityWrapper = new EntityWrapper<T, Serializable>(entityInformation, domainItem);

        return entityWrapper;
    }

    @SuppressWarnings("unchecked")
    private <T> SimpleDbEntityInformation<T, ?> getEntityInformation(Class<?> domainClass) {
        String simpleDbDomain = simpleDb.getSimpleDbDomain().getDomain(domainClass);
        return (SimpleDbEntityInformation<T, ?>) SimpleDbEntityInformationSupport.getMetadata(domainClass,
                simpleDbDomain);
    }

    private <T> void manageSimpleDbDomain(final String domainName) {
        DomainManager.getInstance().manageDomain(domainName, simpleDb.getDomainManagementPolicy(), simpleDbClient);
    }

    private <T> void manageSimpleDbDomains(final SimpleDbEntityInformation<T, ?> entityInformation) {
        List<Field> nestedReferences = ReflectionUtils.getReferenceAttributesList(entityInformation.getJavaType());

        entityInformation.validateReferenceFields(nestedReferences);

        for (Field eachNestedReference : nestedReferences) {
            manageSimpleDbDomain(getDomainName(eachNestedReference.getType()));
        }

        manageSimpleDbDomain(entityInformation.getDomain());
    }
}
