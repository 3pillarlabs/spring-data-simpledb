/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.simpledb.repository.support.entityinformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.reflection.MetadataParser;

public class SimpleDbMetamodelEntityInformation<T, ID extends Serializable> extends
		SimpleDbEntityInformationSupport<T, ID> {

	private final String simpleDbDomain;

	/**
	 * @param domainClass
	 *            must not be {@literal null}.
	 */
	public SimpleDbMetamodelEntityInformation(Class<T> domainClass, String simpleDbDomain) {

		super(domainClass);
		this.simpleDbDomain = simpleDbDomain;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ID getId(T entity) {
		return (ID) MetadataParser.getItemName(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.support.EntityInformation#getIdType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<ID> getIdType() {
		Field idField = MetadataParser.getIdField(getJavaType());
		return (Class<ID>) idField.getType();
	}

	@Override
	public String getDomain() {
		return simpleDbDomain;
	}

	@Override
	public String getItemName(T entity) {
		return MetadataParser.getItemName(entity);
	}

	@Override
	public Map<String, String> getAttributes(T entity) {
		return MetadataParser.getAttributes(entity);
	}

	@Override
	public void validateReferenceFields(List<Field> referenceFields) {
		for(Field eachReferencedField : referenceFields) {
			MetadataParser.validateReferenceAnnotation(eachReferencedField);
		}
	}

	@Override
	public String getItemNameFieldName(T entity) {
		return MetadataParser.getIdField(entity).getName();
	}

	@Override
	public String getAttributesFieldName(T entity) {
		return MetadataParser.getAttributesField(entity).getName();
	}
}
