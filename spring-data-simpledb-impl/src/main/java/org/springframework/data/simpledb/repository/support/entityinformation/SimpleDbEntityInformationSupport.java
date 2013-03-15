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

import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;

/**
 * Base class for {@link SimpleDbEntityInformation} implementations to share common method implementations.
 * 
 */
public abstract class SimpleDbEntityInformationSupport<T, ID extends Serializable> extends
		AbstractEntityInformation<T, ID> implements SimpleDbEntityInformation<T, ID> {

	/**
	 * Creates a new {@link SimpleDbEntityInformationSupport} with the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 */
	public SimpleDbEntityInformationSupport(Class<T> domainClass) {

		super(domainClass);
	}

	/**
	 * Creates a {@link SimpleDbEntityInformation} for the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> SimpleDbEntityInformation<T, ?> getMetadata(Class<T> domainClass, String simpleDbDomain) {
		Assert.notNull(domainClass);
		Assert.notNull(simpleDbDomain);
		
		return new SimpleDbMetamodelEntityInformation(domainClass, simpleDbDomain);
	}

}
