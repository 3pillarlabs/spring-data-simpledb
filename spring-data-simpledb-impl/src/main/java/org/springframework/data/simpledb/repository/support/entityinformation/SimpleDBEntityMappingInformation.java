package org.springframework.data.simpledb.repository.support.entityinformation;

/**
 * Define methods for accessing information related to the entity definition ( e.g. id field name)
 */
public interface SimpleDBEntityMappingInformation<T> {

	String getItemNameFieldName(T entity);

	String getAttributesFieldName(T entity);

}
