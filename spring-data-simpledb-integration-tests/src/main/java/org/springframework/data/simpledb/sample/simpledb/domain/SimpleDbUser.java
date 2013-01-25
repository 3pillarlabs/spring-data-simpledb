package org.springframework.data.simpledb.sample.simpledb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.Database;

import java.util.Map;

@Database(value = "testDB")
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

    @Attributes
    private Map<String, String> atts;


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
}
