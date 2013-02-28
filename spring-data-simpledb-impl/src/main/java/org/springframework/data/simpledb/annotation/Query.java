package org.springframework.data.simpledb.annotation;

import java.lang.annotation.*;

/**
 * Annotation to declare finder queries directly on repository methods. <br/><br/>
 *  
 *  Query Annotation used in Repository Metadata, can have three Types of Parameters <br/>
 *  <p><b>value</b>: if value annotation is present, query string should be correct and well formatted </p>
 * <p><b>select</b>: contains selective attributes, can be independent of the "where" clause; the Domain is inferred at runtime based on Repository-Metadata</p>
 * <p><b>where</b>: contains filtering attributes, can be independent of the "select" clause; the Domain is inferred at runtime based on Repository-Metadata</p>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Query {

    /**
     * Defines the SimpleDb query to be executed when the annotated method is called.
     */
    String value() default "";
    String[] where() default "";
    String[] select() default "";

    public enum QueryClause {
    	SELECT("select"),
    	VALUE("value"),
    	WHERE("where");
    	
    	private String queryClause;
    	
    	private QueryClause(String newValue) {
    		this.queryClause = newValue;
    	}
    	
    	public String getQueryClause() {
    		return this.queryClause;
    	}
    }
}
