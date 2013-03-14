package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

// TODO: validate @Reference for @Id to exist in the parsed class, and check if the annotated Field is really a Nested Type
public class SimpleDbReferences {

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
}
