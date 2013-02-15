package org.springframework.data.simpledb.sample.simpledb.domain;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;

/**
 * TODO: extend with other types to be tested as other type handlers are implemented.
 * One sample for each supported type. Each particular instance and other tests should be included as Regular Junit Tests
 */
public class SimpleDbUser {

    @Id
    private String itemName;

    private float primitiveField;

    private String coreField;

    private NestedEntity nestedEntity;

    private Object objectField;

    private List<Integer> coreTypeList;

    private List<JSONCompatibleClass> objectList;

    private long[] primitiveArrayField;

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }


    public float getPrimitiveField() {
        return primitiveField;
    }

    public void setPrimitiveField(float primitiveField) {
        this.primitiveField = primitiveField;
    }

    public String getCoreField() {
        return coreField;
    }

    public void setCoreField(String coreField) {
        this.coreField = coreField;
    }

    public NestedEntity getNestedEntity() {
        return nestedEntity;
    }

    public void setNestedEntity(NestedEntity nestedEntity) {
        this.nestedEntity = nestedEntity;
    }


    public Object getObjectField() {
        return objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public List<Integer> getCoreTypeList() {
        return coreTypeList;
    }

    public long[] getPrimitiveArrayField() {
        return primitiveArrayField.clone();
    }

    public void setCoreTypeList(List<Integer> coreTypeList) {
        this.coreTypeList = coreTypeList;
    }

    public void setPrimitiveArrayField(long[] primitiveArrayField) {
        this.primitiveArrayField = primitiveArrayField.clone();
    }

    public List<JSONCompatibleClass> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<JSONCompatibleClass> objectList) {
        this.objectList = objectList;
    }

    public static class NestedEntity {

        private int nestedPrimitiveField;

        public int getNestedPrimitiveField() {
            return nestedPrimitiveField;
        }

        public void setNestedPrimitiveField(int nestedPrimitiveField) {
            this.nestedPrimitiveField = nestedPrimitiveField;
        }

        /**
         * Auto-generated
         */
		@Override
		public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(NestedEntity.this);
		}

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(NestedEntity.this, obj);
        }
    }


    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
