package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: extend with other types to be tested as other type handlers are implemented.
 * Also update equals!
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

    private long[] primitiveArrayField;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof SimpleDbUser)) { return false; }

        SimpleDbUser that = (SimpleDbUser) o;

        if (Float.compare(that.primitiveField, primitiveField) != 0) { return false; }
        if (coreField != null ? !coreField.equals(that.coreField) : that.coreField != null) { return false; }
        if (coreTypeList != null ? !coreTypeList.equals(that.coreTypeList) : that.coreTypeList != null) { return false; }
        if (!Arrays.equals(primitiveArrayField, that.primitiveArrayField)) { return false; }
        if (nestedEntity != null ? !nestedEntity.equals(that.nestedEntity) : that.nestedEntity != null) { return false; }
        if (objectField != null ? !objectField.equals(that.objectField) : that.objectField != null) { return false; }

        return true;
    }

    /**
     * Check only on field values (skip the ID)
     */


    @Override
    public int hashCode() {
        int result = itemName != null ? itemName.hashCode() : 0;
        result = 31 * result + (primitiveField != +0.0f ? Float.floatToIntBits(primitiveField) : 0);
        result = 31 * result + (coreField != null ? coreField.hashCode() : 0);
        result = 31 * result + (nestedEntity != null ? nestedEntity.hashCode() : 0);
        result = 31 * result + (objectField != null ? objectField.hashCode() : 0);
        result = 31 * result + (coreTypeList != null ? coreTypeList.hashCode() : 0);
        result = 31 * result + (primitiveArrayField != null ? Arrays.hashCode(primitiveArrayField) : 0);
        return result;
    }

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
			final int prime = 31;
			int result = 1;
			result = prime * result + nestedPrimitiveField;
			return result;
		}

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            NestedEntity other = (NestedEntity) obj;
            if (nestedPrimitiveField != other.nestedPrimitiveField) {
                return false;
            }

            return true;
        }
    }

}
