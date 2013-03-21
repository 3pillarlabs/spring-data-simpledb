package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.lang.reflect.Field;
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
import org.springframework.data.simpledb.exception.InvalidSimpleDBQueryException;
import org.springframework.data.simpledb.parser.SimpleDBParser;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.reflection.MetadataParser;
import org.springframework.data.simpledb.reflection.ReflectionUtils;
import org.springframework.data.simpledb.repository.support.EmptyResultDataAccessException;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.model.*;

/**
 * Primary implementation of {@link SimpleDbOperations}
 */
public class SimpleDbTemplate extends AbstractSimpleDbTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbTemplate.class);

	public SimpleDbTemplate(SimpleDb simpleDb) {
		super(simpleDb);
	}

	@Override
	public <T> T createOrUpdateImpl(T domainItem, EntityWrapper<T, ?> entity) {
		Assert.notNull(entity.getDomain(), "Domain name should not be null");

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
			getDB().putAttributes(request);
		}
		return entity.getItem();
	}

	@Override
	public void deleteAttributesImpl(String domainName, String itemName) {
		LOGGER.debug("Delete Domain\"{}\" ItemName \"{}\"", domainName, itemName);

		Assert.notNull(domainName, "Domain name should not be null");
		Assert.notNull(itemName, "Item name should not be null");

		getDB().deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
	}

	@Override
	public <T> void deleteImpl(T domainItem, boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation,
			EntityWrapper<T, ?> entity) {
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
	public SelectResult invokeFindImpl(boolean consistentRead, String escapedQuery) {
		return getDB().select(new SelectRequest(escapedQuery, consistentRead));
	}

	@Override
	public <T, ID extends Serializable> T readImpl(ID id, Class<T> entityClass, boolean consistentRead,
			SimpleDbEntityInformation<T, ?> entityInformation) {
		LOGGER.debug("Read ItemName \"{}\"", id);

		List<ID> ids = new ArrayList<ID>();
		{
			ids.add(id);
		}
		List<T> results = find(entityClass, new QueryBuilder(entityInformation).withIds(ids).toString(), consistentRead);
		return results.size() == 1 ? results.get(0) : null;
	}

	@Override
	public <T> long countImpl(String query, boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation) {
		LOGGER.debug("Count items for query " + query);
		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);

		final SelectResult selectResult = invokeFindImpl(consistentRead, escapedQuery);
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
	public <T> long countImpl(boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation) {
		final String countQuery = new QueryBuilder(entityInformation, true).toString();
		LOGGER.debug("Count items for query " + countQuery);
		validateSelectQuery(countQuery);

		final SelectResult selectResult = invokeFindImpl(consistentRead, countQuery);
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
	public <T> List<T> findAllQueryImpl(Class<T> entityClass, SimpleDbEntityInformation<T, ?> entityInformation) {
		final String findAllQuery = new QueryBuilder(entityInformation).toString();

		return find(entityClass, findAllQuery);
	}

	@Override
	public <T> Page<T> executePagedQueryImpl(Class<T> entityClass, String query, Pageable pageable,
			boolean consistentRead, SimpleDbEntityInformation<T, ?> entityInformation) {
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

	@Override
	public <T> List<T> recursiveFindImpl(Class<T> entityClass, String query, boolean consistentRead,
			SimpleDbEntityInformation<T, ?> entityInformation) {

		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\"", entityInformation.getDomain(), consistentRead);

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);

		List<T> result = new ArrayList<T>();

		List<String> referenceFieldsNames = ReflectionUtils.getReferencedAttributeNames(entityClass);

		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();

		final SelectResult selectResult = invokeFindImpl(consistentRead, escapedQuery);

		if(referenceFieldsNames.isEmpty()) {
			return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
		}

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
	}

	@Override
	public <T> List<T> findImpl(SimpleDbEntityInformation<T, ?> entityInformation, String query, String nextToken,
			boolean consistentRead) {

		LOGGER.debug("Find All Domain \"{}\" isConsistent=\"{}\", with token!", entityInformation.getDomain(),
				consistentRead);

		final DomainItemBuilder<T> domainItemBuilder = new DomainItemBuilder<T>();

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		SelectRequest selectRequest = new SelectRequest(escapedQuery, consistentRead);

		selectRequest.setNextToken(nextToken);

		final SelectResult selectResult = getDB().select(selectRequest);

		return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
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

		final SelectResult selectResult = getDB().select(new SelectRequest(query, consistentRead));

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
