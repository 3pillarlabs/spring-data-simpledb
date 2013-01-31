package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;

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



    /**
     * Auto-generated
     */
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coreField == null) ? 0 : coreField.hashCode());
		result = prime * result
				+ ((itemName == null) ? 0 : itemName.hashCode());
		result = prime * result
				+ ((nestedEntity == null) ? 0 : nestedEntity.hashCode());
		result = prime * result
				+ ((objectField == null) ? 0 : objectField.hashCode());
		result = prime * result + Float.floatToIntBits(primitiveField);
		
		return result;
	}

    /**
     * Check only on field values (skip the ID)
     */
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

        SimpleDbUser other = (SimpleDbUser) obj;
        if (Float.floatToIntBits(primitiveField) != Float.floatToIntBits(other.primitiveField)) {
            return false;
        }
        if (nestedEntity == null) {
            if (other.nestedEntity != null) {
                return false;
            }
        } else if (!nestedEntity.equals(other.nestedEntity)) {
            return false;
        }

        if (objectField == null) {
            if (other.objectField != null) {
                return false;
            }
        } else if (!objectField.equals(other.objectField)) {
            return false;
        }


        return true;
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
