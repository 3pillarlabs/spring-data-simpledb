package org.springframework.data.simpledb.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;
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
	public void toDomainFieldPrimitive_int_test() throws Exception {
		/* test positive */
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("intField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Integer.parseInt(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Integer.class).toString()) == domainEntity.intField);

		/* test negative */
		domainEntity.intField = Integer.MIN_VALUE;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("intField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Integer.parseInt(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Integer.class).toString()) == domainEntity.intField);
	}

	@Test
	public void toDomainFieldPrimitive_float_test() throws Exception {
		/* test positive */
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) == domainEntity.floatField);

		/* test negative */

		/* TODO: Float.MIN_VALUE is not converted correctly */
		domainEntity.floatField = Float.MIN_VALUE;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) != domainEntity.floatField);

		domainEntity.floatField = Float.MIN_VALUE + 1;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("floatField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Float.class).toString()) == domainEntity.floatField);
	}

	@Test
	public void toDomainFieldPrimitive_double_test() throws Exception {
		/* test positive */
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) == domainEntity.doubleField);

		/* test negative */

		/* TODO: Double.MIN_VALUE is not converted correctly */
		domainEntity.doubleField = Double.MIN_VALUE;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) != domainEntity.doubleField);

		domainEntity.doubleField = -0.000000000001d;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("doubleField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Double.class).toString()) == domainEntity.doubleField);
	}

	@Test
	public void toDomainFieldPrimitive_short_test() throws Exception {
		/* test positive */
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("shortField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Short.parseShort(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Short.class).toString()) == domainEntity.shortField);

		/* test negative */
		domainEntity.shortField = Short.MIN_VALUE;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("shortField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Short.parseShort(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Short.class).toString()) == domainEntity.shortField);
	}

	@Test
	public void toDomainFieldPrimitive_long_test() throws Exception {
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("longField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Long.parseLong(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Long.class).toString()) == domainEntity.longField);
	}

	@Test
	public void toDomainFieldPrimitive_byte_test() throws Exception {
		/* test positive */
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("byteField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Byte.parseByte(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Byte.class).toString()) == domainEntity.byteField);

		/* test negative */
		domainEntity.byteField = Byte.MIN_VALUE;
		convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("byteField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(Byte.parseByte(SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Byte.class).toString()) == domainEntity.byteField);
	}

	@Test
	public void toDomainFieldPrimitive_boolean_test() throws Exception {
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("booleanField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(((Boolean)SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Boolean.class)).equals(domainEntity.booleanField));
	}

	@Test
	public void toDomainFieldPrimitive_date_test() throws Exception {
		String convertedValue = SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField("dateField").get(domainEntity));
		assertNotNull(convertedValue);
		assertTrue(((Date)SimpleDBAttributeConverter.toDomainFieldPrimitive(convertedValue, Date.class)).equals(domainEntity.dateField));
	}
	
	/* ********************* individually test converter methods ********************** */
    @Test
    public void test_padding_long_value(){
        long longValue = 12345L;
        String encoded = SimpleDBAttributeConverter.toSimpleDBAttributeValue(longValue);
        
        assertEquals("09223372036854788153", encoded);
    }
    
    @Test
    public void test_padding_int_value(){
        int intValue = 1;
        String encoded = SimpleDBAttributeConverter.toSimpleDBAttributeValue(intValue);
        
        assertEquals("09223372036854775809", encoded);
    }
    
    @Test
    public void test_encode_int_value() {
    	int x = -390293;
        String encoded;
        BigDecimal bdx;
        
        bdx = new BigDecimal(x);
        encoded = AmazonSimpleDBUtil.encodeRealNumberRange(bdx, 11, new BigDecimal(Integer.MIN_VALUE).negate());
        assertEquals("02147093355", encoded);
    }
    
    @Test
    public void test_decode_int_value() {
    	String encodedInt = "02147093355";
    	BigDecimal bgdecoded = AmazonSimpleDBUtil.decodeRealNumberRange(encodedInt, new BigDecimal(Integer.MIN_VALUE).negate());
        assertEquals(-390293, bgdecoded.intValue());
    }

    @Test
    public void test_encode_and_decode_long() {
        long x;

        x = 209323021234234498L;
        encodeAndDecode(x);

        x = Long.MAX_VALUE;
        encodeAndDecode(x);

        x = Long.MIN_VALUE;
        encodeAndDecode(x);
    }

    @Test
    public void test_encode_and_decode_double() {
        double x;

        x = 500.0;
        encodeAndDecode(x);

        x = 20932304234498.039;
        encodeAndDecode(x);

        x = Double.MAX_VALUE;
        encodeAndDecode(x);

        /* Does not work
        x = Double.MIN_VALUE;
        encodeAndDecode(x); */

        x = -209323021234234498.902938903849082349;
        encodeAndDecode(x);
    }

    @Test
    public void test_encode_and_decode_big_decimal() {
        BigDecimal x;

        x = new BigDecimal("20932304234498.039");
        encodeAndDecode(x);

        x = new BigDecimal("-209323021234234498.902938903849082349");
        encodeAndDecode(x);
    }

    private void encodeAndDecode(double x) {
        encodeAndDecode(new BigDecimal(x));
    }

    private void encodeAndDecode(long x) {
        String encoded;
        BigDecimal bgdecoded;
        BigDecimal bdx = new BigDecimal(x);

        encoded = AmazonSimpleDBUtil.encodeRealNumberRange(bdx, 20, new BigDecimal(Long.MIN_VALUE).negate());
        bgdecoded = AmazonSimpleDBUtil.decodeRealNumberRange(encoded, new BigDecimal(Long.MIN_VALUE).negate());

        assertEquals(bdx, bgdecoded);
    }

    private void encodeAndDecode(BigDecimal bdx) {
        String encoded;
        BigDecimal bgdecoded;
        encoded = AmazonSimpleDBUtil.encodeRealNumberRange(bdx, 20, 20, new BigDecimal(Long.MIN_VALUE).negate());
        bgdecoded = AmazonSimpleDBUtil.decodeRealNumberRange(encoded, 20, new BigDecimal(Long.MIN_VALUE).negate());
        assertTrue(bdx.compareTo(bgdecoded) == 0);
    }

}
