package org.springframework.data.simpledb.query;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.simpledb.core.SimpleDbDomain;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.lang.reflect.Method;

/**
 *
 * An extension of {@link SimpleDbQueryMethod} which determines the whereParameters from the method name.
 * This is a preliminary implementation of the named query! 
 *
 */
public class SimpleDbPartTreeQueryMethod extends SimpleDbQueryMethod {

	private final String whereExpression;
	
	public SimpleDbPartTreeQueryMethod(Method method, RepositoryMetadata metadata, SimpleDbDomain simpleDbDomain) {
		super(method, metadata, simpleDbDomain);
		
		final String domainName = simpleDbDomain.getDomain(metadata.getDomainType());
		final SimpleDbEntityInformation entityInformation = SimpleDbEntityInformationSupport.getMetadata(metadata.getDomainType(), domainName);
		
		whereExpression = PartTreeConverter.toIndexedQuery(new PartTree(method.getName(), entityInformation.getJavaType()));
	}
	
	@Override
	protected String getWhereParameters() {
		return whereExpression;
	}
	
	@Override
	protected String[] getSelectParameters() {
		return new String[] { "" };
	}
	
	@Override
	protected String getValueParameters() {
		return "";
	}
	
}
