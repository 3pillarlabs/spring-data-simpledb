package org.springframework.data.simpledb.core;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.exception.InvalidSimpleDBQueryException;
import org.springframework.data.simpledb.exception.SimpleDbExceptionTranslator;
import org.springframework.data.simpledb.parser.SimpleDBParser;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.reflection.MetadataParser;
import org.springframework.data.simpledb.reflection.ReflectionUtils;
import org.springframework.data.simpledb.repository.support.EmptyResultDataAccessException;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Primary implementation of {@link SimpleDbOperations}
 */
public class SimpleDbTemplate implements SimpleDbOperations {

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

	public SimpleDb getSimpleDb() {
		return simpleDb;
	}

	@Override
	public String getDomainName(Class<?> entityClass) {
		return simpleDb.getDomain(entityClass);
	}

	@Override
	public <T> T createOrUpdate(T domainItem) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(domainItem.getClass());
		final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem, entityInformation);

		Assert.notNull(entity.getDomain(), "Domain name should not be null");

		manageSimpleDbDomains(entityInformation);

		logOperation("Create or update", entity);
		entity.generateIdIfNotSet();

		for(final Field field : ReflectionUtils.getFirstLevelOfReferenceAttributes(domainItem.getClass())) {
			final Object referenceEntity = ReflectionUtils.callGetter(domainItem, field.getName());

			/* recursive call */
			if(referenceEntity != null) {
				createOrUpdate(referenceEntity);
			}
		}

		delete(entity.getDomain(), entity.getItemName());

		Map<String, String> rawAttributes = entity.serialize();
		List<PutAttributesRequest> putAttributesRequests = SimpleDbRequestBuilder.createPutAttributesRequests(
				entity.getDomain(), entity.getItemName(), rawAttributes);

		for(PutAttributesRequest request : putAttributesRequests) {
			try {
				simpleDbClient.putAttributes(request);
			} catch(AmazonClientException amazonException) {
				throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
			}
		}

		return entity.getItem();
	}

	@Override
	public void delete(String domainName, String itemName) {
		manageSimpleDbDomain(domainName);

		LOGGER.debug("Delete Domain\"{}\" ItemName \"{}\"", domainName, itemName);

		Assert.notNull(domainName, "Domain name should not be null");
		Assert.notNull(itemName, "Item name should not be null");

		try {
			simpleDbClient.deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

	@Override
	public <T> void delete(T entity) {
		delete(entity, simpleDb.isConsistentRead());
	}

	@Override
	public <T> void delete(T domainItem, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(domainItem.getClass());
		final EntityWrapper<T, ?> entity = getEntityWrapper(domainItem, entityInformation);

		if(consistentRead) {
			T persistedEntity = read(entity.getItemName(), entityInformation.getJavaType(), consistentRead);

			if(persistedEntity == null) {
				throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!",
						entityInformation.getJavaType(), entity.getItemName()));
			}
		}

		for(final Field field : ReflectionUtils.getFirstLevelOfReferenceAttributes(domainItem.getClass())) {
			final Object referenceEntity = ReflectionUtils.callGetter(domainItem, field.getName());

			/* recursive call */
			if(referenceEntity != null) {
				delete(referenceEntity, consistentRead);
			}
		}

		delete(entity.getDomain(), entity.getItemName());
	}

	@Override
	public <T> void deleteAll(Class<T> entityClass) {
		deleteAll(entityClass, simpleDb.isConsistentRead());
	}

	@Override
	public <T> void deleteAll(Class<T> entityClass, boolean consistentRead) {
		for(T element : findAll(entityClass, consistentRead)) {
			delete(element);
		}
	}

	@Override
	public <T, ID extends Serializable> T read(ID id, Class<T> entityClass) {
		return read(id, entityClass, simpleDb.isConsistentRead());
	}

	@Override
	public <T, ID extends Serializable> T read(ID id, Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

		manageSimpleDbDomains(entityInformation);

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
	public <T> long count(String query, Class<T> entityClass) {
		return count(query, entityClass, simpleDb.isConsistentRead());
	}

	@Override
	public <T> long count(String query, Class<T> entityClass, boolean consistentRead) {
		LOGGER.debug("Count items for query " + query);

		validateSelectQuery(query);

		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

		manageSimpleDbDomains(entityInformation);

		final String escapedQuery = getEscapedQuery(query, entityInformation);

		try {
			final SelectResult selectResult = simpleDbClient.select(new SelectRequest(escapedQuery, consistentRead));
			for(Item item : selectResult.getItems()) {
				if(item.getName().equals("Domain")) {
					for(Attribute attribute : item.getAttributes()) {
						if(attribute.getName().equals("Count")) {
							return Long.parseLong(attribute.getValue());
						}
					}
				}
			}
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}

		return 0;
	}

	@Override
	public <T> long count(Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);
		final String countQuery = new QueryBuilder(entityInformation, true).toString();

		manageSimpleDbDomains(entityInformation);

		LOGGER.debug("Count items for query " + countQuery);

		validateSelectQuery(countQuery);

		try {
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
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
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

		manageSimpleDbDomains(entityInformation);

		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\"", entityInformation.getDomain(), consistentRead);

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);

		List<T> result = new ArrayList<T>();

		List<String> referenceFieldsNames = ReflectionUtils.getReferencedAttributeNames(entityClass);

		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();

		final SelectResult selectResult = simpleDbClient.select(new SelectRequest(escapedQuery, consistentRead));

		if(referenceFieldsNames.isEmpty()) {
			return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
		}

		try {

			for(Item item : selectResult.getItems()) {

				T populatedItem = domainItemBuilder.populateDomainItem(entityInformation, item);

				result.add(populatedItem);
				for(Attribute attribute : item.getAttributes()) {
					if(!referenceFieldsNames.contains(attribute.getName())) {
						continue;
					}

					Class<?> referenceEntityClazz = ReflectionUtils.getFieldClass(entityClass, attribute.getName());
					Object referenceEntity = read(attribute.getValue(), referenceEntityClazz);

					ReflectionUtils.callSetter(populatedItem, attribute.getName(), referenceEntity);

				}
			}

			return result;

		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		return findAll(entityClass, simpleDb.isConsistentRead());
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass, boolean consistentRead) {
		final SimpleDbEntityInformation<T, ?> entityInformation = getEntityInformation(entityClass);

		manageSimpleDbDomains(entityInformation);

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

		final String countQuery = new QueryBuilder(escapedQuery, true).toString();

		Long totalCount = count(countQuery, entityClass, consistentRead);

		return new PageImpl<T>(resultsList, pageable, totalCount);
	}

	private <T> List<T> find(SimpleDbEntityInformation<T, ?> entityInformation, String query, String nextToken,
			boolean consistentRead) {

		manageSimpleDbDomains(entityInformation);

		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\", with token!", entityInformation.getDomain(),
				consistentRead);

		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		SelectRequest selectRequest = new SelectRequest(escapedQuery, consistentRead);

		selectRequest.setNextToken(nextToken);

		try {
			final SelectResult selectResult = simpleDbClient.select(selectRequest);

			return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

	private <T> EntityWrapper<T, ?> getEntityWrapper(T domainItem, SimpleDbEntityInformation<T, ?> entityInformation) {
		final EntityWrapper<T, ?> entityWrapper = new EntityWrapper<T, Serializable>(entityInformation, domainItem);

		return entityWrapper;
	}

	@SuppressWarnings("unchecked")
	private <T> SimpleDbEntityInformation<T, ?> getEntityInformation(Class<?> domainClass) {
		String simpleDbDomain = simpleDb.getSimpleDbDomain().getDomain(domainClass);
		return (SimpleDbEntityInformation<T, ?>) SimpleDbEntityInformationSupport.getMetadata(domainClass,
				simpleDbDomain);
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

		try {
			final SelectResult selectResult = simpleDbClient.select(new SelectRequest(query, consistentRead));

			return selectResult.getNextToken();
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
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

	private <T> void manageSimpleDbDomain(final String domainName) {
		DomainManager.getInstance().manageDomain(domainName, simpleDb.getDomainManagementPolicy(), simpleDbClient);
	}

	private <T> void manageSimpleDbDomains(final SimpleDbEntityInformation<T, ?> entityInformation) {
		List<Field> nestedReferences = ReflectionUtils.getReferenceAttributesList(entityInformation.getJavaType());

		entityInformation.validateReferenceFields(nestedReferences);

		for(Field eachNestedReference : nestedReferences) {
			manageSimpleDbDomain(getDomainName(eachNestedReference.getType()));
		}

		manageSimpleDbDomain(entityInformation.getDomain());
	}
}
