package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

/**
 * Primary implementation of {@link ISimpleDBOperations}
 */
public class SimpleDBTemplate implements ISimpleDBOperations {

	private SimpleDb simpleDb;

	public SimpleDBTemplate(SimpleDb simpleDb) {
		Assert.notNull(simpleDb);
		this.simpleDb = simpleDb;
	}

	@Override
	public AmazonSimpleDB getDB() {
		return simpleDb.getSimpleDbClient();
	}

    public SimpleDb getSimpleDb(){
        return simpleDb;
    }

	@Override
	public String getDomainName(Class<?> entityClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T createOrUpdateItem(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteItem(String domainName, String itemName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T, ID extends Serializable> T readItem(ID id, Class<T> entityClass, boolean consistentRead) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> long count(Class<T> entityClass, boolean consistentRead) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<T> find(Class<T> entityClass, String query, boolean consistentRead) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable, boolean consistentRead) {
		throw new UnsupportedOperationException();
	}

}
