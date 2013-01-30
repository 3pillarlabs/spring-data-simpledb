package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;

import java.util.Map;

/**
 * TODO: extend with other types to be tested as other type handlers are implemented.
 * Also update equals! 
 */
public class SimpleDbUser {

    @Id
    private String itemName;

    private int intField;
    private float floatField;
    private double doubleField;
    private short shortField;
    private boolean booleanField;
    private long longField;
    private byte byteField;
    private String stringField;
    
    private NestedBClass nestedB;

    @Attributes
    private Map<String, String> atts;

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
		if (booleanField != other.booleanField) {
			return false;
		}
		if (byteField != other.byteField) {
			return false;
		}
		if (Double.doubleToLongBits(doubleField) != Double.doubleToLongBits(other.doubleField)) {
			return false;
		}
		if (Float.floatToIntBits(floatField) != Float.floatToIntBits(other.floatField)) {
			return false;
		}
		if (intField != other.intField) {
			return false;
		}
		if (longField != other.longField) {
			return false;
		}
		if (nestedB == null) {
			if (other.nestedB != null) {
				return false;
			}
		} else if (!nestedB.equals(other.nestedB)) {
			return false;
		}
		if (shortField != other.shortField) {
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

    public int getIntField() {
		return intField;
	}
	public void setIntField(int intField) {
		this.intField = intField;
	}
	
	public float getFloatField() {
		return floatField;
	}
	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}
	
	public double getDoubleField() {
		return doubleField;
	}
	public void setDoubleField(double doubleField) {
		this.doubleField = doubleField;
	}
	
	public short getShortField() {
		return shortField;
	}
	public void setShortField(short shortField) {
		this.shortField = shortField;
	}
	
	public boolean getBooleanField() {
		return booleanField;
	}
	public void setBooleanField(boolean booleanField) {
		this.booleanField = booleanField;
	}
	
	public long getLongField() {
		return longField;
	}
	public void setLongField(long longField) {
		this.longField = longField;
	}
	
	public byte getByteField() {
		return byteField;
	}
	public void setByteField(byte byteField) {
		this.byteField = byteField;
	}
	
	public String getStringField() {
		return stringField;
	}
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
	
	public NestedBClass getNestedB() {
		return nestedB;
	}
	public void setNestedB(NestedBClass nestedB) {
		this.nestedB = nestedB;
	}
	
	public static class NestedBClass {
		
		private int intField;
		private NestedCClass nestedNestedC;
		
		public NestedCClass getNestedNestedC() {
			return nestedNestedC;
		}
		public void setNestedNestedC(NestedCClass nestedNestedC) {
			this.nestedNestedC = nestedNestedC;
		}
		
		public int getIntField() {
			return intField;
		}
		public void setIntField(int intField) {
			this.intField = intField;
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
			NestedBClass other = (NestedBClass) obj;
			if (intField != other.intField) {
				return false;
			}
			if (nestedNestedC == null) {
				if (other.nestedNestedC != null) {
					return false;
				}
			} else if (!nestedNestedC.equals(other.nestedNestedC)) {
				return false;
			}
			
			return true;
		}

		public static class NestedCClass {
			private double doubleField;
			
			public double getDoubleField() {
				return doubleField;
			}
			public void setDoubleField(double doubleField) {
				this.doubleField = doubleField;
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
				NestedCClass other = (NestedCClass) obj;
				if (Double.doubleToLongBits(doubleField) != Double.doubleToLongBits(other.doubleField)) {
					return false;
				}
				
				return true;
			}
			
		}
	}
}
