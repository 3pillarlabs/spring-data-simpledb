package org.springframework.data.simpledb.core;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public class QueryBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilder.class);

	private StringBuilder query;

	public QueryBuilder(SimpleDbEntityInformation<?, ?> entityInformation) {
		this(entityInformation, false);
	}

	public QueryBuilder(SimpleDbEntityInformation<?, ?> entityInformation, boolean shouldCount) {
		query = new StringBuilder();
		query.append("select");

		if(shouldCount) {
			query.append(" count(*) ");
		} else {
			query.append(" * ");
		}

		query.append("from ").append(quote(entityInformation.getDomain()));
	}

	public QueryBuilder(String customQuery) {
		this(customQuery, false);
	}

	public QueryBuilder(String customQuery, boolean shouldCount) {
		this.query = new StringBuilder();

		if(shouldCount) {
			query.append("select count(*) from ").append(customQuery.split("from")[1]);
		} else {
			query.append(customQuery);
		}
	}

	public QueryBuilder withLimit(final int limit) {
		query.append(" limit ").append(limit);

		return this;
	}

	public QueryBuilder withIds(Iterable<?> iterable) {
		Iterator<?> iterator = iterable.iterator();
		appendWhereOrEndClause(query);

		query.append("(");
		while(iterator.hasNext()) {
			query.append("itemName()='").append(iterator.next().toString()).append("'");
			if(iterator.hasNext()) {
				query.append(" or ");
			}
		}
		query.append(")");
		return this;
	}

	public QueryBuilder with(Sort sort) {
		if(sort != null) {
			Iterator<Sort.Order> sortIt = sort.iterator();
			if(sortIt.hasNext()) {
				Sort.Order order = sortIt.next();
				appendWhereOrEndClause(query);
				query.append(order.getProperty()).append(" is not null order by ");
				query.append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase());
			}
			if(sortIt.hasNext()) {
				throw new IllegalArgumentException("SimpleDb does not support multiple sorting");
			}
		}

		return this;
	}

	public QueryBuilder with(Pageable pageable) {
		Sort sort = pageable.getSort();
		if(sort != null) {
			with(sort);
		}

		if(pageable.getPageSize() > 0) {
			withLimit(pageable.getPageSize());
		}

		return this;
	}

	@Override
	public String toString() {
		// TODO change itemName() to ID field from domain object
		String result = query.toString();
		LOGGER.debug("Created query: {}", result);
		return result;
	}

	private void appendWhereOrEndClause(StringBuilder query) {
		if(query.indexOf("where") > 0) {
			query.append(" and ");
		} else {
			query.append(" where ");
		}
	}

	private String quote(String simpleDbName) {
		return "`" + simpleDbName + "`";
	}

}
