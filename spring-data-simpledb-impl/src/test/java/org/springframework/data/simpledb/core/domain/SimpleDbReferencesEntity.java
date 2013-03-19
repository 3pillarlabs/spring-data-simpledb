package org.springframework.data.simpledb.core.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

public class SimpleDbReferencesEntity {

	@Id
	String itemName;

	@Reference
	Double notNestedDouble;

	@Reference
	FirstNestedEntity firstNestedEntity;

	public static class FirstNestedEntity {

		@Reference
		SecondNestedEntity secondNestedEntity;
	}

	public static class SecondNestedEntity {

	}
}
