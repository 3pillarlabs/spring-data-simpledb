package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.parser.SimpleDBParser;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

/**
 * Primary implementation of {@link ISimpleDbOperations}
 */
public class SimpleDbTemplate implements ISimpleDbOperations {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbTemplate.class);
	
	private final SimpleDb simpleDb;
	private final AmazonSimpleDB simpleDbClient;

	public SimpleDbTemplate(SimpleDb simpleDb) {
		Assert.notNull(simpleDb);
		this.simpleDb = simpleDb;
		this.simpleDbClient = simpleDb.getSimpleDbClient();
	}

	@Override
	public AmazonSimpleDB getDB() {
		return simpleDb.getSimpleDbClient();
	}

	@Override
	public String getDomainName(Class<?> entityClass) {
		return simpleDb.getDomain(entityClass);
	}

	@Override
	public <T> T createOrUpdate(T domainItem) {
		final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem);
		
		Assert.notNull(entity.getDomain(), "Domain name should not be null");
		
		logOperation("Create or update", entity);
		entity.generateIdIfNotSet();

		delete(entity.getDomain(), entity.getItemName());

		Map<String, String> rawAttributes = entity.serialize();
		List<PutAttributesRequest> putAttributesRequests = SimpleDbRequestBuilder.createPutAttributesRequests(
				entity.getDomain(), entity.getItemName(), rawAttributes);

		for(PutAttributesRequest request : putAttributesRequests) {
			simpleDbClient.putAttributes(request);
		}

		return entity.getItem();
	}

	@Override
	public void delete(String domainName, String itemName) {
		LOGGER.debug("Delete Domain\"{}\" ItemName \"{}\"", domainName, itemName);
		
		Assert.notNull(domainName, "Domain name should not be null");
		Assert.notNull(itemName, "Item name should not be null");
		simpleDbClient.deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
	}

	@Override
	public <T> void delete(T domainItem) {
		final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem);
		
		delete(entity.getDomain(), entity.getItemName());
	}
	
	@Override
	public <T, ID extends Serializable> T read(ID id, Class<T> entityClass) {
		return read(id, entityClass, simpleDb.isConsistentRead());
	}
	
	@Override
	public <T, ID extends Serializable> T read(ID id, Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		
		LOGGER.debug("Read ItemName \"{}\"", id);
		
		List<ID> ids = new ArrayList<ID>();
		{
			ids.add(id);
		}
		List<T> results = find(entityClass, new QueryBuilder(entityInformation).withIds(ids).toString(), consistentRead);
		return results.size() == 1 ? results.get(0) : null;
	}

	@Override
	public <T> long count(Class<T> entityClass) {
		return count(entityClass, simpleDb.isConsistentRead());
	}
	
	@Override
	public <T> long count(Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		final String countQuery = new QueryBuilder(entityInformation, true).toString();
		
		LOGGER.debug("Count items for query " + countQuery);

		validateSelectQuery(countQuery);

		final SelectResult selectResult = simpleDbClient.select(new SelectRequest(countQuery, consistentRead));
		for(Item item : selectResult.getItems()) {
			if(item.getName().equals("Domain")) {
				for(Attribute attribute : item.getAttributes()) {
					if(attribute.getName().equals("Count")) {
						return Long.parseLong(attribute.getValue());
					}
				}
			}
		}
		return 0;
	}

	@Override
	public <T> List<T> find(Class<T> entityClass, String query) {
		return find(entityClass, query, simpleDb.isConsistentRead());
	}
	
	@Override
	public <T> List<T> find(Class<T> entityClass, String query, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();
		
		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\"", entityInformation.getDomain(), consistentRead);

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		final SelectResult selectResult = simpleDbClient.select(new SelectRequest(escapedQuery, consistentRead));

		return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
	}
	
	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		return findAll(entityClass, simpleDb.isConsistentRead());
	}
	
	@Override
	public <T> List<T> findAll(Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		final String findAllQuery = new QueryBuilder(entityInformation).toString();
		
		return find(entityClass, findAllQuery);
	}

	@Override
	public <T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable) {
		return executePagedQuery(entityClass, query, pageable, simpleDb.isConsistentRead());
	}
	
	@Override
	public <T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		
		Assert.notNull(pageable);
		Assert.isTrue(pageable.getPageNumber() > 0);
		Assert.isTrue(pageable.getPageSize() > 0);

		final String escapedQuery = getEscapedQuery(query, entityInformation);

		List<T> resultsList;
		String queryWithPageSizeLimit = new QueryBuilder(escapedQuery).with(pageable).toString();

		if(pageable.getPageNumber() != 1) {
			String pageOffsetToken = getPageOffsetToken(pageable, entityInformation, escapedQuery, consistentRead);

			if(pageOffsetToken != null && !pageOffsetToken.isEmpty()) {
				resultsList = find(entityInformation, queryWithPageSizeLimit, pageOffsetToken, consistentRead);
			} else {
				resultsList = Collections.emptyList();
			}

		} else {
			resultsList = find(entityClass, queryWithPageSizeLimit, consistentRead);
		}

		Long totalCount = count(entityClass, consistentRead);

		return new PageImpl<T>(resultsList, pageable, totalCount);
	}

	private <T> List<T> find(SimpleDbEntityInformation<T, ?> entityInformation, String query, String nextToken,
			boolean consistentRead) {
		
		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\", with token!", entityInformation.getDomain(),
				consistentRead);
		
		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		SelectRequest selectRequest = new SelectRequest(escapedQuery, consistentRead);

		selectRequest.setNextToken(nextToken);

		final SelectResult selectResult = simpleDbClient.select(selectRequest);

		return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
	}
	
	private <T> EntityWrapper<T, ?> getEntityWrapper(T domainItem) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(domainItem.getClass());
		final EntityWrapper<T, ?> entityWrapper = new EntityWrapper<T, Serializable>(entityInformation, domainItem);
		
		return entityWrapper;
	}

	@SuppressWarnings("unchecked")
	private <T> SimpleDbEntityInformation<T, ?> getEntityInformation(Class<?> domainClass) {
		return (SimpleDbEntityInformation<T, ?>) SimpleDbEntityInformationSupport.getMetadata(domainClass);
	}
	
	private <T> String getEscapedQuery(String query, SimpleDbEntityInformation<T, ?> entityInformation) {
		return QueryUtils.escapeQueryAttributes(query, MetadataParser.getIdField(entityInformation.getJavaType())
				.getName());
	}
	
	/*
	 * Validate a custom query before sending the request to the DB.
	 */
	private void validateSelectQuery(final String selectQuery) {
		final SimpleDBParser parser = new SimpleDBParser(selectQuery);
		try {
			parser.selectQuery();
		} catch(Exception e) {
			throw new InvalidSimpleDBQueryException("The following query is an invalid SimpleDB query: " + selectQuery,
					e);
		}
	}
	
	private String getNextToken(String query, boolean consistentRead) {
		LOGGER.debug("Get next token for query: " + query);

		Assert.isTrue(query.contains("limit"), "Only queries with limit have a next token!");

		final SelectResult selectResult = simpleDbClient.select(new SelectRequest(query, consistentRead));

		return selectResult.getNextToken();
	}

	private <T> String getPageOffsetToken(final Pageable pageable, SimpleDbEntityInformation<T, ?> entityInformation,
			String query, boolean consistentRead) {
		int previousPageNumber = pageable.getPageNumber() - 1;
		int endOfPreviousPageLimit = previousPageNumber * pageable.getPageSize();

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		final String countQuery = new QueryBuilder(escapedQuery, true).withLimit(endOfPreviousPageLimit).toString();

		return getNextToken(countQuery, consistentRead);
	}
	
	private void logOperation(String operation, EntityWrapper<?, ?> entity) {
		LOGGER.debug(operation + " \"{}\" ItemName \"{}\"", entity.getDomain(), entity.getItemName());
	}

}
