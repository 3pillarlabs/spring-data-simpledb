package org.springframework.data.simpledb.query.executions;

/**
 * Enum for different type of queries returning a collection result.
 */
public enum MultipleResultType {
    COLLECTION_OF_DOMAIN_ENTITIES,
    LIST_BASED_REPRESENTATION,
    FIELD_OF_TYPE_COLLECTION,
    LIST_OF_FIELDS,
    SET_OF_FIELDS;

}
