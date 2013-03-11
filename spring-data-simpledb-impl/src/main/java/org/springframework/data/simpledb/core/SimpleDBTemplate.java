package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

/**
 * Primary implementation of {@link ISimpleDBOperations} 
 */
public class SimpleDBTemplate implements ISimpleDBOperations {

	private AmazonSimpleDBClient sdb;

	public SimpleDBTemplate(final String accessID, final String secretKey) {
		sdb = new AmazonSimpleDBClient(new AWSCredentials() {

			@Override
			public String getAWSAccessKeyId() {
				return accessID;
			}

			@Override
			public String getAWSSecretKey() {
				return secretKey;
			}
		});
	}
	
	@Override
	public AmazonSimpleDB getDB() {
		return sdb;
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
