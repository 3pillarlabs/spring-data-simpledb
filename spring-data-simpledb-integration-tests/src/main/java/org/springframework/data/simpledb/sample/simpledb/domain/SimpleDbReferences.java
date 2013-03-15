package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

public class SimpleDbReferences {

	@Id
	private String itemName;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public FirstNestedEntity getFirstNestedEntity() {
		return firstNestedEntity;
	}

	public void setFirstNestedEntity(FirstNestedEntity firstNestedEntity) {
		this.firstNestedEntity = firstNestedEntity;
	}

	@Reference
	private FirstNestedEntity firstNestedEntity;

	public static class FirstNestedEntity {

		@Id
		private String itemName;

		@Reference
		private SecondNestedEntity secondNestedEntity;

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public SecondNestedEntity getSecondNestedEntity() {
			return secondNestedEntity;
		}

		public void setSecondNestedEntity(SecondNestedEntity secondNestedEntity) {
			this.secondNestedEntity = secondNestedEntity;
		}
	}

	public static class SecondNestedEntity {

		@Id
		private String itemName;
	}
}
