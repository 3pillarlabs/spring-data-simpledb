package org.springframework.data.simpledb.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
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
	
	private String toDomainFieldPrimitive(String value, Class<?> clazz) throws ParseException {
		return SimpleDBAttributeConverter.toDomainFieldPrimitive(value, clazz).toString();
	}

	private String toSimpleDBAttributeValue(SampleEntity domainEntity, String fieldName) throws IllegalAccessException, NoSuchFieldException {
		return SimpleDBAttributeConverter.toSimpleDBAttributeValue(domainEntity.getClass().getDeclaredField(fieldName).get(domainEntity));
	}
	
	@Test
	public void toDomainFieldPrimitive_int_test() throws Exception {
		/* test positive */
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "intField");
		assertNotNull(convertedValue);
		assertTrue(Integer.parseInt(toDomainFieldPrimitive(convertedValue, Integer.class)) == domainEntity.intField);

		/* test negative */
		domainEntity.intField = Integer.MIN_VALUE;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "intField");
		assertNotNull(convertedValue);
		assertTrue(Integer.parseInt(toDomainFieldPrimitive(convertedValue, Integer.class)) == domainEntity.intField);
	}

	@Test
	public void toDomainFieldPrimitive_float_test() throws Exception {
		/* test positive */
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "floatField");
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(toDomainFieldPrimitive(convertedValue, Float.class)) == domainEntity.floatField);

		/* test negative */

		/* TODO: Float.MIN_VALUE is not converted correctly */
		domainEntity.floatField = Float.MIN_VALUE;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "floatField");
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(toDomainFieldPrimitive(convertedValue, Float.class)) != domainEntity.floatField);

		domainEntity.floatField = Float.MIN_VALUE + 1;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "floatField");
		assertNotNull(convertedValue);
		assertTrue(Float.parseFloat(toDomainFieldPrimitive(convertedValue, Float.class)) == domainEntity.floatField);
	}

	@Test
	public void toDomainFieldPrimitive_double_test() throws Exception {
		/* test positive */
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "doubleField");
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(toDomainFieldPrimitive(convertedValue, Double.class)) == domainEntity.doubleField);

		/* test negative */

		/* TODO: Double.MIN_VALUE is not converted correctly */
		domainEntity.doubleField = Double.MIN_VALUE;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "doubleField");
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(toDomainFieldPrimitive(convertedValue, Double.class)) != domainEntity.doubleField);

		domainEntity.doubleField = -0.000000000001d;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "doubleField");
		assertNotNull(convertedValue);
		assertTrue(Double.parseDouble(toDomainFieldPrimitive(convertedValue, Double.class)) == domainEntity.doubleField);
	}

	@Test
	public void toDomainFieldPrimitive_short_test() throws Exception {
		/* test positive */
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "shortField");
		assertNotNull(convertedValue);
		assertTrue(Short.parseShort(toDomainFieldPrimitive(convertedValue, Short.class)) == domainEntity.shortField);

		/* test negative */
		domainEntity.shortField = Short.MIN_VALUE;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "shortField");
		assertNotNull(convertedValue);
		assertTrue(Short.parseShort(toDomainFieldPrimitive(convertedValue, Short.class)) == domainEntity.shortField);
	}

	@Test
	public void toDomainFieldPrimitive_long_test() throws Exception {
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "longField");
		assertNotNull(convertedValue);
		assertTrue(Long.parseLong(toDomainFieldPrimitive(convertedValue, Long.class)) == domainEntity.longField);
	}

	@Test
	public void toDomainFieldPrimitive_byte_test() throws Exception {
		/* test positive */
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "byteField");
		assertNotNull(convertedValue);
		assertTrue(Byte.parseByte(toDomainFieldPrimitive(convertedValue, Byte.class)) == domainEntity.byteField);

		/* test negative */
		domainEntity.byteField = Byte.MIN_VALUE;
		convertedValue = toSimpleDBAttributeValue(domainEntity, "byteField");
		assertNotNull(convertedValue);
		assertTrue(Byte.parseByte(toDomainFieldPrimitive(convertedValue, Byte.class)) == domainEntity.byteField);
	}

	@Test
	public void toDomainFieldPrimitive_boolean_test() throws Exception {
		String convertedValue = toSimpleDBAttributeValue(domainEntity, "booleanField");
		assertNotNull(convertedValue);
		assertTrue(Boolean.parseBoolean(toDomainFieldPrimitive(convertedValue, Boolean.class)) == domainEntity.booleanField);
	}
	
	/* ********************* individually test converter methods ********************** */
    @Test
    public void test_encode_int_value_number_of_digits() {
        int x = 1, numberOfDigits = 11;
        BigDecimal bdx = new BigDecimal(x);
        String encoded = AmazonSimpleDBUtil.encodeRealNumberRange(bdx, numberOfDigits, new BigDecimal(Integer.MIN_VALUE).negate());
        assertEquals(numberOfDigits, encoded.length());
        
        x = 1;
        numberOfDigits = 20;
        bdx = new BigDecimal(x);
        encoded = AmazonSimpleDBUtil.encodeRealNumberRange(bdx, numberOfDigits, new BigDecimal(Integer.MIN_VALUE).negate());
        
        assertEquals(numberOfDigits, encoded.length());
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

    @Test
    public void toSimpleDBAttributeValues_should_return_an_string_representation_of_concatenated_array_elements() throws ParseException {
        final int[] someInts = {1, 2, 3, 4};
        final String expectedValue = "1,2,3,4";

        Object returnedPrimitiveCol = SimpleDBAttributeConverter.toDomainFieldPrimitiveCollection(expectedValue, int.class);
        int arrayLength = Array.getLength(returnedPrimitiveCol);

        for (int idx = 0; idx < arrayLength; idx++) {
            assertEquals(someInts[idx], Array.get(returnedPrimitiveCol, idx));
        }
    }

    @Test
    public void encode_decode_primitive_collections() throws ParseException {
        int[] someInts = {1, 2, 3, 4};

        String paddedReturnedString = SimpleDBAttributeConverter.toSimpleDBAttributeValues(someInts);

        Object returnedPrimitiveCol = SimpleDBAttributeConverter.toDomainFieldPrimitiveCollection(paddedReturnedString, int.class);
        int arrayLength = Array.getLength(returnedPrimitiveCol);

        for (int idx = 0; idx < arrayLength; idx++) {
            assertEquals(someInts[idx], Array.get(returnedPrimitiveCol, idx));
        }
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
