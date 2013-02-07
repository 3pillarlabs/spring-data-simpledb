package org.springframework.data.simpledb.core;


public class SampleEntity {
	private int intField;
	private float floatField;
	private double doubleField;
	private short shortField;
	private long longField;
	private byte byteField;
	private boolean booleanField;
   private String stringField;
   private Double doubleWrapper;

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

	public boolean getBooleanField() {
		return booleanField;
	}
	public void setBooleanField(boolean booleanField) {
		this.booleanField = booleanField;
	}

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Double getDoubleWrapper() {
        return doubleWrapper;
    }

    public void setDoubleWrapper(Double doubleWrapper) {
        this.doubleWrapper = doubleWrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SampleEntity)) return false;

        SampleEntity that = (SampleEntity) o;

        if (booleanField != that.booleanField) return false;
        if (byteField != that.byteField) return false;
        if (Double.compare(that.doubleField, doubleField) != 0) return false;
        if (Float.compare(that.floatField, floatField) != 0) return false;
        if (intField != that.intField) return false;
        if (longField != that.longField) return false;
        if (shortField != that.shortField) return false;
        if (doubleWrapper != null ? !doubleWrapper.equals(that.doubleWrapper) : that.doubleWrapper != null)
            return false;
        if (stringField != null ? !stringField.equals(that.stringField) : that.stringField != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = intField;
        result = 31 * result + (floatField != +0.0f ? Float.floatToIntBits(floatField) : 0);
        temp = doubleField != +0.0d ? Double.doubleToLongBits(doubleField) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) shortField;
        result = 31 * result + (int) (longField ^ (longField >>> 32));
        result = 31 * result + (int) byteField;
        result = 31 * result + (booleanField ? 1 : 0);
        result = 31 * result + (stringField != null ? stringField.hashCode() : 0);
        result = 31 * result + (doubleWrapper != null ? doubleWrapper.hashCode() : 0);
        return result;
    }
}
