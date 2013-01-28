package org.springframework.data.simpledb.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SimpleDBAttributeConverter {

    public static final int LONG_DIGITS = 20;
    public static final BigDecimal OFFSET_VALUE = new BigDecimal(Long.MIN_VALUE).negate();

    private static String padOrConvertIfRequired(Object ob) {
        if (ob instanceof Integer || ob instanceof Long || ob instanceof Short || ob instanceof Byte) {
            // then pad
            return AmazonSimpleDBUtil.encodeRealNumberRange(new BigDecimal(ob.toString()), AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        } else if ((ob instanceof Double && !((Double) ob).isInfinite() && !((Double) ob).isNaN()) || (ob instanceof Float && !((Float) ob).isInfinite() && !((Float) ob).isNaN())) {
            // then pad
            return AmazonSimpleDBUtil.encodeRealNumberRange(new BigDecimal(ob.toString()), AmazonSimpleDBUtil.LONG_DIGITS, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        } else if (ob instanceof BigDecimal) {
            // then pad
            return AmazonSimpleDBUtil.encodeRealNumberRange((BigDecimal) ob, AmazonSimpleDBUtil.LONG_DIGITS, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        } else if (ob instanceof Date) {
            Date d = (Date) ob;
            return AmazonSimpleDBUtil.encodeDate(d);
        } else if (ob instanceof byte[]) {
            return AmazonSimpleDBUtil.encodeByteArray((byte[]) ob);
        }

        return ob.toString();
    }

    public static String toSimpleDBAttributeValue(final Object fieldValue) {
        return padOrConvertIfRequired(fieldValue);
    }

    public static String toSimpleDBAttributeValues(final Object primitiveCollectionFieldValues) {
        final StringBuilder attributeValuesBuilder = new StringBuilder();
        int primitiveCollLength = Array.getLength(primitiveCollectionFieldValues);

        for (int idx = 0; idx < primitiveCollLength; idx++) {
            Object itemValue = Array.get(primitiveCollectionFieldValues, idx);
            attributeValuesBuilder.append(padOrConvertIfRequired(itemValue));

            if(idx < primitiveCollLength - 1) {
                attributeValuesBuilder.append(",");
            }
        }

        return attributeValuesBuilder.toString();
    }

    public static List<String> toSimpleDBAttribute(final Collection<?> fieldValues) {
        final List<String> result = new ArrayList<>();

        if (fieldValues != null) {

            Object val = null;
            for (final Iterator<?> it = fieldValues.iterator(); it.hasNext(); val = it.next()) {
                result.add(toSimpleDBAttributeValue(val));
            }
        }

        return result;
    }

    public static Object toDomainFieldPrimitive(String value, Class<?> retType) throws ParseException {
        Object val = null;

        if (Integer.class.isAssignableFrom(retType) || retType == int.class) {
            val = AmazonSimpleDBUtil.decodeRealNumberRange(value, OFFSET_VALUE).intValue();
        } else if (Long.class.isAssignableFrom(retType) || retType == long.class) {
            val = AmazonSimpleDBUtil.decodeRealNumberRange(value, OFFSET_VALUE).longValue();
        }
        if (Short.class.isAssignableFrom(retType) || retType == short.class) {
            val = AmazonSimpleDBUtil.decodeRealNumberRange(value, OFFSET_VALUE).shortValue();
        } else if (Byte.class.isAssignableFrom(retType) || retType == byte.class) {
            val = AmazonSimpleDBUtil.decodeRealNumberRange(value, OFFSET_VALUE).byteValue();
        } else if (Float.class.isAssignableFrom(retType) || retType == float.class) {
            // Ignore NaN and Infinity
            if (!value.matches(".*Infinity|NaN")) {
                val = AmazonSimpleDBUtil.decodeRealNumberRange(value, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE).floatValue();
            } else {
                val = Float.NaN;
            }
        } else if (Double.class.isAssignableFrom(retType) || retType == double.class) {
            // Ignore NaN and Infinity
            if (!value.matches(".*Infinity|NaN")) {
                val = AmazonSimpleDBUtil.decodeRealNumberRange(value, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE).doubleValue();
            } else {
                val = Double.NaN;
            }
        } else if (BigDecimal.class.isAssignableFrom(retType)) {
            val = AmazonSimpleDBUtil.decodeRealNumberRange(value, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        } else if (byte[].class.isAssignableFrom(retType)) {
            val = AmazonSimpleDBUtil.decodeByteArray(value);
        } else if (Date.class.isAssignableFrom(retType)) {
            val = AmazonSimpleDBUtil.decodeDate(value);
        } else if (Boolean.class.isAssignableFrom(retType) || retType == boolean.class) {
            val = Boolean.parseBoolean(value);
        } else if (String.class.isAssignableFrom(retType)) {
            val = value;
        }

        return val;
    }

    public static Object toDomainFieldPrimitiveCollection(String value, Class<?> retType) throws ParseException {
        List<String> splitedValues = StringUtil.splitStringByDelim(value, ",");
        Object primitiveCollection = Array.newInstance(retType, splitedValues.size());

        for (int idx = 0; idx < splitedValues.size(); idx++) {
            Array.set(primitiveCollection, idx, toDomainFieldPrimitive(splitedValues.get(idx), retType));
        }

        return primitiveCollection;
    }
}