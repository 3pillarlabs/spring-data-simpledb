package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * TODO: use <br>, add link to QUery 
 *  Acts as a Parser for Building the Custom Query based on the given Query parameters<br/><br/>
 *  
 *  Query Annotation used in Repository Metadata, can have three Types of Parameters
 *  <p><b>value</b>: if value annotation is present, query string should be correct and well formatted </p>
 * <p><b>select</b>: contains selective attributes, can be independent of the "where" clause; the Domain is inferred at runtime based on Repository-Metadata</p>
 * <p><b>where</b>: contains filtering attributes, can be independent of the "select" clause; the Domain is inferred at runtime based on Repository-Metadata</p>
 *
 */
public abstract class AbstractQueryParser {
	/**
	 * This hash is used as a shared data structure, holding the fieldName and it's corresponding rawParameterExpression
	 * The use-case of this Map<FieldName, RawParameter>  is for validating that the parameters within the WHERE clause
	 * should appear as parameters also in the SELECT clause
	 * It is represented as a synchronized Map Instance
	 */
	
	// TODO: rename hash -> Map
	protected Map<String, String> fieldNameWithParamHash;
    
    public static String buildQueryFromQueryParameters(String valueParameter, String[] rawSelectParameters, String[] rawWhereParameters, Class<?> domainClass){
        if(StringUtils.hasText(valueParameter)){
            return valueParameter;
        }
        
//        assertMatchingSelectAndWhereClauses();
//        StringBuilder stringBuilder = new StringBuilder();
//        appendSelectClause(stringBuilder, rawSelectParameters);
//        appendFromClause()
//        appendWhereClauseIfPresent()
//        
//        return stringB.toString();
        

        StringBuilder stringBuilder = new StringBuilder();
        SelectQueryParser selectQuery = null;
        if(StringUtils.hasText(rawSelectParameters[0])){
            selectQuery = new SelectQueryParser(rawSelectParameters, domainClass);
            stringBuilder.append(selectQuery.createSelectClause());
        } else {
            stringBuilder.append("select *");
        }
        
        stringBuilder.append(" from `"+ MetadataParser.getDomain(domainClass)+"`");

        if(StringUtils.hasText(rawWhereParameters[0])){
            WhereQueryParser whereQuery = new WhereQueryParser(rawWhereParameters, domainClass);
            
            stringBuilder.append(whereQuery.createWhereClause());
            
            whereQuery.assertWhereFieldsAreInSelectClause(selectQuery);
        }

        return stringBuilder.toString();
    }
   
   protected String createQueryClause(String clause, String wherePattern, Class<?> domainClazz, String[] rawParameters, String delimiter) {
        StringBuilder query = new StringBuilder(clause);
        Field idField = MetadataParser.getIdField(domainClazz);
        int idx = 1;
        
        for (Map.Entry<String, String> eachEntry : fieldNameWithParamHash.entrySet()) {
            String replacedParameter = replaceField(eachEntry.getKey(), eachEntry.getValue(), idField);
        	query.append(replacedParameter);
        	
        	if(idx++ != rawParameters.length) {
        		query.append(delimiter);
        	}
        }
        
        return query.toString();
    }

	protected void assertThatFieldsDeclaredInClass(Class<?> domainClazz) {
		for (Map.Entry<String, String> eachEntry : fieldNameWithParamHash.entrySet()) {
			boolean isFieldDeclared = ReflectionUtils.isFieldInClass(domainClazz, eachEntry.getKey());
			Assert.isTrue(isFieldDeclared, "no such field in entity class : " + eachEntry.getKey());
		}
	}
	
	protected Map<String, String> getFieldNameWithParamHash() {
		return fieldNameWithParamHash;
	}
	
	protected void assertWhereFieldsAreInSelectClause(AbstractQueryParser queryStrategy) {
		throw new NotImplementedException();
	}
	
    protected abstract String replaceField(String fieldName, String rawParameter, Field idField);
    
}