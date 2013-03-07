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
 *
 */
public class SimpleDbOperationsImpl<T, ID extends Serializable> implements SimpleDbOperations<T, ID> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbOperationsImpl.class);
	private final AmazonSimpleDB sdb;
	private final DomainItemBuilder domainItemBuilder;

	public SimpleDbOperationsImpl(AmazonSimpleDB sdb) {
		this.sdb = sdb;
		domainItemBuilder = new DomainItemBuilder<T, ID>();
	}

	@Override
	public Object updateItem(EntityWrapper entity) {
		logOperation("Update", entity);
		Assert.notNull(entity.getDomain(), "Domain name should not be null");
		entity.generateIdIfNotSet();

		deleteItem(entity.getDomain(), entity.getItemName());

		Map<String, String> rawAttributes = entity.serialize();
		List<PutAttributesRequest> putAttributesRequests = SimpleDbRequestBuilder.createPutAttributesRequests(
				entity.getDomain(), entity.getItemName(), rawAttributes);

		for(PutAttributesRequest request : putAttributesRequests) {
			sdb.putAttributes(request);
		}

		return entity.getItem();
	}

	@Override
	public void deleteItem(String domainName, String itemName) {
		LOGGER.info("Delete Domain\"{}\" ItemName \"{}\"", domainName, itemName);
		Assert.notNull(domainName, "Domain name should not be null");
		Assert.notNull(itemName, "Item name should not be null");
		sdb.deleteAttributes(new DeleteAttributesRequest(domainName, itemName));
	}

	@Override
	public T readItem(SimpleDbEntityInformation<T, ID> entityInformation, ID id, boolean consistentRead) {
		LOGGER.info("Read ItemName \"{}\"", id);
		List<ID> ids = new ArrayList<ID>();
		{
			ids.add(id);
		}
		List<T> results = find(entityInformation, new QueryBuilder(entityInformation).withIds(ids), consistentRead);
		return results.size() == 1 ? results.get(0) : null;
	}

	@Override
	public List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, QueryBuilder queryBuilder,
			boolean consistentRead) {
		return find(entityInformation, queryBuilder.toString(), consistentRead);
	}

	@Override
	public List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, String query, boolean consistentRead) {
		LOGGER.info("Find All Domain \"{}\" isConsistent=\"{}\"", entityInformation.getDomain(), consistentRead);

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		final SelectResult selectResult = sdb.select(new SelectRequest(escapedQuery, consistentRead));

		return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
	}

	private String getEscapedQuery(String query, SimpleDbEntityInformation<T, ID> entityInformation) {
		return QueryUtils.escapeQueryAttributes(query, MetadataParser.getIdField(entityInformation.getJavaType())
				.getName());
	}

	private List<T> find(SimpleDbEntityInformation<T, ID> entityInformation, String query, String nextToken,
			boolean consistentRead) {
		LOGGER.info("Find All Domain \"{}\" isConsistent=\"{}\", with token!", entityInformation.getDomain(),
				consistentRead);

		validateSelectQuery(query);

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		SelectRequest selectRequest = new SelectRequest(escapedQuery, consistentRead);

		selectRequest.setNextToken(nextToken);

		final SelectResult selectResult = sdb.select(selectRequest);

		return domainItemBuilder.populateDomainItems(entityInformation, selectResult);
	}

	@Override
	public long count(SimpleDbEntityInformation entityInformation, boolean consistentRead) {
		LOGGER.info("Count items from domain \"{}\" isConsistent=\"{}\"", entityInformation.getDomain(), consistentRead);
		return count(new QueryBuilder(entityInformation, true).toString(), consistentRead);
	}

	// TODO: escape bounded query with itemName() for every item_id
	@Override
	public long count(String query, boolean consistentRead) {
		LOGGER.info("Count items for query " + query);

		validateSelectQuery(query);

		final SelectResult selectResult = sdb.select(new SelectRequest(query, consistentRead));
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

	private String getNextToken(String query, boolean consistentRead) {
		LOGGER.info("Get next token for query: " + query);

		Assert.isTrue(query.contains("limit"), "Only queries with limit have a next token!");

		final SelectResult selectResult = sdb.select(new SelectRequest(query, consistentRead));

		return selectResult.getNextToken();
	}

	private String getPageOffsetToken(final Pageable pageable, SimpleDbEntityInformation<T, ID> entityInformation,
			String query, boolean consistentRead) {
		int previousPageNumber = pageable.getPageNumber() - 1;
		int endOfPreviousPageLimit = previousPageNumber * pageable.getPageSize();

		final String escapedQuery = getEscapedQuery(query, entityInformation);
		final String countQuery = new QueryBuilder(escapedQuery, true).withLimit(endOfPreviousPageLimit).toString();

		return getNextToken(countQuery, consistentRead);
	}

	@Override
	public Page<T> executePagedQuery(SimpleDbEntityInformation<T, ID> entityInformation, String query,
			Pageable pageable, boolean consistentRead) {
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
			resultsList = find(entityInformation, queryWithPageSizeLimit, consistentRead);
		}

		final String countQuery = new QueryBuilder(escapedQuery, true).toString();

		Long totalCount = count(countQuery, consistentRead);

		return new PageImpl<T>(resultsList, pageable, totalCount);
	}

	private void logOperation(String operation, EntityWrapper<T, ID> entity) {
		LOGGER.info(operation + " \"{}\" ItemName \"{}\"", entity.getDomain(), entity.getItemName());
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
}
