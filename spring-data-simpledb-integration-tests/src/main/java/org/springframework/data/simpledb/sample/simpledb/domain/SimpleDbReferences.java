package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

public class SimpleDbReferences {

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

    @Id
	private String itemName;

	@Reference
	private FirstNestedEntity firstNestedEntity;

	public static class FirstNestedEntity {

        public SecondNestedEntity getSecondNestedEntity() {
            return secondNestedEntity;
        }

        public void setSecondNestedEntity(SecondNestedEntity secondNestedEntity) {
            this.secondNestedEntity = secondNestedEntity;
        }

        @Reference
		private SecondNestedEntity secondNestedEntity;
	}

	public static class SecondNestedEntity {

	}
}
