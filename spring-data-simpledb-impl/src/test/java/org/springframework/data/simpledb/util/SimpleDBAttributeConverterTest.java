package org.springframework.data.simpledb.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class SimpleDBAttributeConverterTest {

	static class SampleEntity {
		int intField;
		float floatField;
		double doubleField;
		short shortField;
		long longField;
		byte byteField;
		boolean booleanField;
		
		Date dateField;
	}
	
	private SampleEntity domainEntity;
	
	@Before
	public void setUp() {
		domainEntity = new SampleEntity();
		
		domainEntity.intField = Integer.MAX_VALUE;
		domainEntity.floatField = Float.MAX_VALUE;
		domainEntity.doubleField = Double.MAX_VALUE;
		domainEntity.shortField = Short.MAX_VALUE;
		domainEntity.longField = Long.MAX_VALUE;
		domainEntity.byteField = Byte.MAX_VALUE;
		domainEntity.booleanField = true;
		domainEntity.dateField = new Date(200);
	}
	
	@Test
	public void toDomainFieldPrimitive_int_test() {
		try {
			/* test positive */
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("intField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Integer.parseInt(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Integer.class).toString()) == domainEntity.intField);
			
			/* test negative */
			domainEntity.intField = Integer.MIN_VALUE;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("intField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Integer.parseInt(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Integer.class).toString()) == domainEntity.intField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_float_test() {
		try {
			/* test positive */
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) == domainEntity.floatField);
			
			/* test negative */
			
			/* TODO: Float.MIN_VALUE is not converted correctly */
			domainEntity.floatField = Float.MIN_VALUE;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) != domainEntity.floatField);
			
			domainEntity.floatField = Float.MIN_VALUE + 1;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) == domainEntity.floatField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_double_test() {
		try {
			/* test positive */
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) == domainEntity.doubleField);
			
			/* test negative */
			
			/* TODO: Double.MIN_VALUE is not converted correctly */
			domainEntity.doubleField = Double.MIN_VALUE;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) != domainEntity.doubleField);
			
			domainEntity.doubleField = -0.000000000001d;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) == domainEntity.doubleField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_short_test() {
		try {
			/* test positive */
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("shortField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Short.parseShort(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Short.class).toString()) == domainEntity.shortField);
			
			/* test negative */
			domainEntity.shortField = Short.MIN_VALUE;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("shortField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Short.parseShort(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Short.class).toString()) == domainEntity.shortField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_long_test() {
		try {
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("longField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Long.parseLong(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Long.class).toString()) == domainEntity.longField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_byte_test() {
		try {
			/* test positive */
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("byteField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Byte.parseByte(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Byte.class).toString()) == domainEntity.byteField);
			
			/* test negative */
			domainEntity.byteField = Byte.MIN_VALUE;
			convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("byteField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(Byte.parseByte(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Byte.class).toString()) == domainEntity.byteField);
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_boolean_test() {
		try {
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("booleanField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(((Boolean)SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Boolean.class)).equals(domainEntity.booleanField));
		} catch (Exception e) {
			fail();
		} 
	}
	
	@Test
	public void toDomainFieldPrimitive_date_test() {
		try {
			String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttribute(domainEntity.getClass().getDeclaredField("dateField").get(domainEntity));
			assertNotNull(convertedValue);
			assertTrue(((Date)SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Date.class)).equals(domainEntity.dateField));
		} catch (Exception e) {
			fail();
		} 
	}
	
}
