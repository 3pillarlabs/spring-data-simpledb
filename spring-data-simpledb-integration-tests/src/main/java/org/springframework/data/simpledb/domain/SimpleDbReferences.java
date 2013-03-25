package org.springframework.data.simpledb.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

public class SimpleDbReferences {

    @Id
	private String itemName;

	@Reference
	private FirstNestedEntity firstNestedEntity;
	
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
    
	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
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
        
    	@Override
    	public boolean equals(Object o) {
    		return EqualsBuilder.reflectionEquals(this, o);
    	}

    	@Override
    	public int hashCode() {
    		return HashCodeBuilder.reflectionHashCode(this);
    	}

    	@Override
    	public String toString() {
    		return ToStringBuilder.reflectionToString(this);
    	}
    }
    
    public static class SecondNestedEntity {

        @Id
    	private String itemName;
        
        private String primitive = "primitive_string";
        
    	public String getItemName() {
    		return itemName;
    	}
    	public void setItemName(String itemName) {
    		this.itemName = itemName;
    	}
    	
    	public String getPrimitive() {
    		return primitive;
    	}
    	
    	public void setPrimitive(String primitive) {
    		this.primitive = primitive;
    	}
    	
    	@Override
    	public boolean equals(Object o) {
    		return EqualsBuilder.reflectionEquals(this, o);
    	}

    	@Override
    	public int hashCode() {
    		return HashCodeBuilder.reflectionHashCode(this);
    	}

    	@Override
    	public String toString() {
    		return ToStringBuilder.reflectionToString(this);
    	}
    }
}
