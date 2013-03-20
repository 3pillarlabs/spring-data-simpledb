/*
 * Copyright 2011 the original author or authors.
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

import org.springframework.data.repository.core.EntityInformation;

/**
 * Extension of {@link org.springframework.data.repository.core.EntityInformation} to capture aditional JPA specific
 * information about entities.
 * 
 */
public interface SimpleDbEntityInformation<T, ID extends Serializable> extends EntityInformation<T, ID>,
		SimpleDBEntityMappingInformation<T> {

	String getDomain();

	String getItemName(T entity);

	Map<String, String> getAttributes(T entity);

	void validateReferenceFields(List<Field> referenceFields);
}
