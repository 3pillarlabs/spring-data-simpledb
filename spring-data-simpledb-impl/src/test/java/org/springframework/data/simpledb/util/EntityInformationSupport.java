package org.springframework.data.simpledb.util;

import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;


public final class EntityInformationSupport {

	private EntityInformationSupport() {
		/* utility class */
	}
	
	@SuppressWarnings("unchecked")
	public static <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
		return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E> getMetadata(clazz, "");
	}
}
