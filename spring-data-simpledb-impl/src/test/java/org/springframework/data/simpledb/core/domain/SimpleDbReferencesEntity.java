package org.springframework.data.simpledb.core.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class SimpleDbReferencesEntity {

	@Id
	String itemName;

    @Reference
    FirstNestedEntity firstNestedEntity;

	public static class FirstNestedEntity {

        @Reference
		SecondNestedEntity secondNestedEntity;
	}

	public static class SecondNestedEntity {

	}

	public static SimpleDbEntityInformation<SimpleDbReferencesEntity, String> entityInformation() {
		return (SimpleDbEntityInformation<SimpleDbReferencesEntity, String>) SimpleDbEntityInformationSupport.getMetadata(
                SimpleDbReferencesEntity.class, "simpleDbReferenceEntity");
	}
}
