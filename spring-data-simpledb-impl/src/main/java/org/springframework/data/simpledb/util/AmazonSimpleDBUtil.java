package org.springframework.data.simpledb.util;

/**
 * *****************************************************************************
 * Copyright 2007 Amazon Technologies, Inc. Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License. You may obtain a copy of the License at: http://aws.amazon.com/apache2.0 This file is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * ***************************************************************************** __ _ _ ___ ( )( \/\/ )/ __) /__\ \ / \__ \ (_)(_) \/\/ (___/
 *
 * Amazon Simple DB Java Library API Version: 2007-11-07 Generated: Fri Jan 18 01:13:17 PST 2008
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.springframework.data.mapping.model.MappingException;

/**
 * Provides collection of static functions for conversion of various values into strings that may be compared lexicographically.
 *
 */
public final class AmazonSimpleDBUtil {

    /**
     * static value hardcoding date format used for conversation of Date into String
     */
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final int LONG_DIGITS = 20;
    private static final BigDecimal OFFSET_VALUE = new BigDecimal(Long.MIN_VALUE).negate();
    private static final String UTF8_ENCODING = "UTF-8";
    private static final int BASE = 10;
    private static final int ENCODE_DATE_COLONS_INDEX = 2;
    private static final int DECODE_DATE_COLONS_INDEX = 3;


    private AmazonSimpleDBUtil() {
    	/* utility class */
    }


    /**
     * Unsed to encode an Integer {@link Number}.
     */
    public static String encodeAsIntegerNumber(Object ob) {
        BigDecimal  integerBigDecimal = AmazonSimpleDBUtil.tryToStoreAsIntegerBigDecimal(ob);
        if(integerBigDecimal != null){
            return AmazonSimpleDBUtil.encodeRealNumberRange(integerBigDecimal, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        }

        return null;
    }


    public static BigDecimal decodeIntegerNumber(String value){
        return AmazonSimpleDBUtil.decodeRealNumberRange(value, OFFSET_VALUE);
    }

    /**
     * Unsed to encode a Not Integer {@link Number}.
     */
    public static String encodeAsRealNumber(Object ob) {
        if(AmazonSimpleDBUtil.isNaN(ob) || AmazonSimpleDBUtil.isInfinite(ob)){
            throw new MappingException("Could not serialize NaN or Infinity values");
        }


        BigDecimal realBigDecimal = AmazonSimpleDBUtil.tryToStoreAsRealBigDecimal(ob);
        if(realBigDecimal != null){
            return AmazonSimpleDBUtil.encodeRealNumberRange(realBigDecimal, AmazonSimpleDBUtil.LONG_DIGITS, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
        }

        return null;
    }

    public static BigDecimal decodeRealNumber(String value){
        if(value.matches(".*Infinity|NaN")){
            throw new MappingException("Could not serialize NaN or Infinity values");
        }
        
        return AmazonSimpleDBUtil.decodeRealNumberRange(value, AmazonSimpleDBUtil.LONG_DIGITS, OFFSET_VALUE);
    }


    public static String encodeRealNumberRange(BigDecimal number, int maxNumDigits, BigDecimal offsetValue) {
        final BigDecimal offsetNumber = number.add(offsetValue);
        final String longString = offsetNumber.toString();
        final int numZeroes = maxNumDigits - longString.length();
        final int paddedSize = numZeroes + longString.length();
        final StringBuilder strBuffer = new StringBuilder(paddedSize);
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        
        return strBuffer.toString();
    }

    public static String encodeRealNumberRange(BigDecimal number, int maxDigitsLeft, int maxDigitsRight, BigDecimal offsetValue) {
        BigDecimal shiftMultiplier = new BigDecimal(Math.pow(BASE, maxDigitsRight));
        BigDecimal shiftedNumber = number.multiply(shiftMultiplier);
        shiftedNumber = shiftedNumber.setScale(0, BigDecimal.ROUND_HALF_UP);
        final BigDecimal shiftedOffset = offsetValue.multiply(shiftMultiplier);
        final BigDecimal offsetNumber = shiftedNumber.add(shiftedOffset);
        String longString = offsetNumber.toString();
        final int numBeforeDecimal = longString.length();
        final int numZeroes = maxDigitsLeft + maxDigitsRight - numBeforeDecimal;
        final int paddedSize = numZeroes + longString.length();
        final StringBuilder strBuffer = new StringBuilder(paddedSize);
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    public static BigDecimal decodeRealNumberRange(String value, BigDecimal offsetValue) {
        BigDecimal offsetNumber = new BigDecimal(value);
        return (offsetNumber.subtract(offsetValue));
    }


    public static BigDecimal decodeRealNumberRange(String value, int maxDigitsRight, BigDecimal offsetValue) {
        BigDecimal offsetNumber = new BigDecimal(value);
        BigDecimal shiftMultiplier = new BigDecimal(Math.pow(BASE, maxDigitsRight));
        BigDecimal tempVal0 = offsetValue.multiply(shiftMultiplier);
        BigDecimal tempVal = (offsetNumber.subtract(tempVal0));
        return (tempVal.divide(shiftMultiplier));
    }

    /**
     * Encodes date value into string format that can be compared lexicographically
     *
     * @param date date value to be encoded
     * @return string representation of the date value
     */
    public static String encodeDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        /* Java doesn't handle ISO8601 nicely: need to add ':' manually */
        String result = dateFormatter.format(date);
        return result.substring(0, result.length() - ENCODE_DATE_COLONS_INDEX) + ":" + result.substring(result.length() - ENCODE_DATE_COLONS_INDEX);
    }

    /**
     * Decodes date value from the string representation created using encodeDate(..) function.
     *
     * @param	value	string representation of the date value
     * @return	original date value
     */
    public static Date decodeDate(String value) throws ParseException {
        String javaValue = value.substring(0, value.length() - DECODE_DATE_COLONS_INDEX) + value.substring(value.length() - ENCODE_DATE_COLONS_INDEX);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.parse(javaValue);
    }

    /**
     * Encodes byteArray value into a base64-encoded string.
     *
     * @return string representation of the date value
     */
    public static String encodeByteArray(byte[] byteArray) {
        try {
            return new String(Base64.encodeBase64(byteArray), UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
             throw new MappingException("Could not encode byteArray to UTF8 encoding", e);
        }
    }

    /**
     * Decodes byte[] value from the string representation created using encodeDate(..) function.
     *
     * @param value string representation of the date value
     * @return original byte[] value
     */
    public static byte[] decodeByteArray(String value) throws ParseException {
        try {
            return Base64.decodeBase64(value.getBytes(UTF8_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new MappingException("Could not decode byteArray to UTF8 encoding", e);
        }

    }

    private static BigDecimal tryToStoreAsRealBigDecimal(Object ob){
        BigDecimal bigDecimal = null;
        if(canBeStoredAsRealBigDecimal(ob)){
            bigDecimal =  new BigDecimal(ob.toString());
        }  else if (ob instanceof BigDecimal){
            bigDecimal = (BigDecimal) ob;
        }

        return bigDecimal;
    }

    private static BigDecimal tryToStoreAsIntegerBigDecimal(Object ob){
        BigDecimal bigDecimal = null;
        if(canBeStoredAsIntegerBigDecimal(ob)){
            bigDecimal =  new BigDecimal(ob.toString());
        }

        return bigDecimal;
    }

    private static boolean canBeStoredAsRealBigDecimal(Object ob){
        if(isNaN(ob) || isInfinite(ob)){
            return false;
        }

        return ob instanceof Double || ob instanceof Float;
    }


    private static boolean canBeStoredAsIntegerBigDecimal(Object ob){
        return ob instanceof Number && !(ob instanceof Float || ob instanceof Double);
    }


    public static boolean isNaN(Object ob) {
        return (ob instanceof Double  && ((Double) ob).isNaN()) ||
                (ob instanceof Float  && ((Float) ob).isNaN());
    }

    public static boolean isInfinite(Object ob) {
        return (ob instanceof Double  && ((Double) ob).isInfinite()) ||
                (ob instanceof Float  && ((Float) ob).isInfinite());
    }

}
