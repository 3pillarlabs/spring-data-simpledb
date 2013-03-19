package org.springframework.data.simpledb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.simpledb.core.QueryBuilder;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;


public class PartTreeSimpleDbQuery implements RepositoryQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartTreeSimpleDbQuery.class);
	private final SimpleDbQueryMethod method;
	private final SimpleDbOperations simpleDbOperations;

	private final PartTree partTree;
	private SimpleDbEntityInformation<?, ?> entityInformation;
	
	public PartTreeSimpleDbQuery(SimpleDbQueryMethod method, SimpleDbOperations simpleDbOperations, SimpleDbEntityInformation<?, ?> entityInformation) {
		this.method = method;
		this.simpleDbOperations = simpleDbOperations;
		this.entityInformation = entityInformation;
		
		partTree = new PartTree(method.getName(), entityInformation.getJavaType());
		
		LOGGER.debug("Constructed part tree {}", partTree);
	}
	
	@Override
	public Object execute(Object[] parameters) {
		final QueryBuilder queryBuilder = new QueryBuilder(entityInformation, partTree, parameters);
		
		return null;
	}

	@Override
	public QueryMethod getQueryMethod() {
		return method;
	}

}
