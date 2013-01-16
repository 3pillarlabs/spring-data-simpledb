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
package org.springframework.data.simpledb.repository.support;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.persistence.IdClass;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of {@link org.springframework.data.repository.core.EntityInformation} that uses JPA {@link javax.persistence.metamodel.Metamodel}
 * to find the domain class' id field.
 *
 * @author Oliver Gierke
 */
public class SimpleDbMetamodelEntityInformation<T, ID extends Serializable> extends SimpleDbEntityInformationSupport<T, ID> {

//	private final IdMetadata<T> idMetadata;

	/**
	 * Creates a new {@link org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation} for the given domain class and {@link javax.persistence.metamodel.Metamodel}.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public SimpleDbMetamodelEntityInformation(Class<T> domainClass) {

		super(domainClass);

//		ManagedType<T> type = metamodel.managedType(domainClass);
//
//		if (type == null) {
//			throw new IllegalArgumentException("The given domain class can not be found in the given Metamodel!");
//		}
//
//		if (!(type instanceof IdentifiableType)) {
//			throw new IllegalArgumentException("The given domain class does not contain an id attribute!");
//		}
//
//		this.idMetadata = new IdMetadata<T>((IdentifiableType<T>) type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public ID getId(T entity) {
        return (ID) null;
//		BeanWrapper entityWrapper = new DirectFieldAccessFallbackBeanWrapper(entity);
//
//		if (idMetadata.hasSimpleId()) {
//			return (ID) entityWrapper.getPropertyValue(idMetadata.getSimpleIdAttribute().getName());
//		}
//
//		BeanWrapper idWrapper = new DirectFieldAccessFallbackBeanWrapper(idMetadata.getType());
//		boolean partialIdValueFound = false;
//
//		for (SingularAttribute<? super T, ?> attribute : idMetadata) {
//			Object propertyValue = entityWrapper.getPropertyValue(attribute.getName());
//
//			if (propertyValue != null) {
//				partialIdValueFound = true;
//			}
//
//			idWrapper.setPropertyValue(attribute.getName(), propertyValue);
//		}
//
//		return (ID) (partialIdValueFound ? idWrapper.getWrappedInstance() : null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.data.repository.support.EntityInformation#getIdType()
	 */
	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		return (Class<ID>) String.class;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.jpa.repository.support.JpaEntityMetadata#
	 * getIdAttribute()
	 */
	public SingularAttribute<? super T, ?> getIdAttribute() {
        return null;
//		return idMetadata.getSimpleIdAttribute();
	}

//	/**
//	 * Simple value object to encapsulate id specific metadata.
//	 *
//	 * @author Oliver Gierke
//	 */
//	private static class IdMetadata<T> implements Iterable<SingularAttribute<? super T, ?>> {
//
//		private final IdentifiableType<T> type;
//		private final Set<SingularAttribute<? super T, ?>> attributes;
//
//		@SuppressWarnings("unchecked")
//		public IdMetadata(IdentifiableType<T> source) {
//
//			this.type = source;
//			this.attributes = (Set<SingularAttribute<? super T, ?>>) (source.hasSingleIdAttribute() ? Collections
//					.singleton(source.getId(source.getIdType().getJavaType())) : source.getIdClassAttributes());
//		}
//
//		public boolean hasSimpleId() {
//			return attributes.size() == 1;
//		}
//
//		public Class<?> getType() {
//
//			try {
//				return type.getIdType().getJavaType();
//			} catch (IllegalStateException e) {
//				// see https://hibernate.onjira.com/browse/HHH-6951
//				IdClass annotation = type.getJavaType().getAnnotation(IdClass.class);
//				return annotation == null ? null : annotation.value();
//			}
//		}
//
//		public SingularAttribute<? super T, ?> getSimpleIdAttribute() {
//			return attributes.iterator().next();
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see java.lang.Iterable#iterator()
//		 */
//		public Iterator<SingularAttribute<? super T, ?>> iterator() {
//			return attributes.iterator();
//		}
//	}
//
//	/**
//	 * Custom extension of {@link org.springframework.beans.BeanWrapperImpl} that falls back to direct field access in case the object or type being
//	 * wrapped does not use accessor methods.
//	 *
//	 * @author Oliver Gierke
//	 */
//	private static class DirectFieldAccessFallbackBeanWrapper extends BeanWrapperImpl {
//
//		public DirectFieldAccessFallbackBeanWrapper(Object entity) {
//			super(entity);
//		}
//
//		public DirectFieldAccessFallbackBeanWrapper(Class<?> type) {
//			super(type);
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.springframework.beans.BeanWrapperImpl#getPropertyValue(java.lang.String)
//		 */
//		@Override
//		public Object getPropertyValue(String propertyName) {
//			try {
//				return super.getPropertyValue(propertyName);
//			} catch (NotReadablePropertyException e) {
//				Field field = ReflectionUtils.findField(getWrappedClass(), propertyName);
//				ReflectionUtils.makeAccessible(field);
//				return ReflectionUtils.getField(field, getWrappedInstance());
//			}
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.springframework.beans.BeanWrapperImpl#setPropertyValue(java.lang.String, java.lang.Object)
//		 */
//		@Override
//		public void setPropertyValue(String propertyName, Object value) {
//			try {
//				super.setPropertyValue(propertyName, value);
//			} catch (NotWritablePropertyException e) {
//				Field field = ReflectionUtils.findField(getWrappedClass(), propertyName);
//				ReflectionUtils.makeAccessible(field);
//				ReflectionUtils.setField(field, getWrappedInstance(), value);
//			}
//		}
//	}
}
