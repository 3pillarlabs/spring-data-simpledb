package org.springframework.data.simpledb.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.util.Assert;

/**
 * This class is used to get information about query field names and execute queries
 */
public class SimpleDbQueryRunner {

	private final SimpleDbOperations simpledbOperations;
	private final Class<?> domainClass;
	private final String query;
	private Pageable pageable;

	public SimpleDbQueryRunner(SimpleDbOperations simpledbOperations, Class<?> domainClass, String query) {
		this.simpledbOperations = simpledbOperations;
		this.domainClass = domainClass;
		this.query = query;
	}

	public SimpleDbQueryRunner(SimpleDbOperations simpledbOperations, Class<?> domainClass, String query,
			Pageable pageable) {
		this(simpledbOperations, domainClass, query);

		Assert.notNull(pageable);
		Assert.isTrue(pageable.getPageNumber() > 0);
		Assert.isTrue(pageable.getPageSize() > 0);

		this.pageable = pageable;
	}

	public List<?> executeQuery() {
		return simpledbOperations.find(domainClass, query);
	}

	public Object executeSingleResultQuery() {
		List<?> returnListFromDb = executeQuery();

		return getSingleResult(returnListFromDb);
	}

	Object getSingleResult(List<?> returnListFromDb) {
		Assert.isTrue(returnListFromDb.size() <= 1,
				"Select statement should return only one entity from database, returned elements size="
						+ returnListFromDb.size() + ", for Query=" + query);

		return returnListFromDb.size() > 0 ? returnListFromDb.get(0) : null;
	}

	public long executeCount() {
		return simpledbOperations.count(query, domainClass);
	}

	public List<String> getRequestedQueryFieldNames() {
		return QueryUtils.getQueryPartialFieldNames(query);
	}

	public String getSingleQueryFieldName() {
		List<String> queryFieldNames = getRequestedQueryFieldNames();
		Assert.isTrue(queryFieldNames.size() == 1);
		return queryFieldNames.get(0);
	}

	public Page<?> executePagedQuery() {
		return simpledbOperations.executePagedQuery(domainClass, query, pageable);
	}

}
