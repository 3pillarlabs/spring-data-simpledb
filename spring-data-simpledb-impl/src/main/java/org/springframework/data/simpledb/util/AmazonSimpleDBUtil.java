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

import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides collection of static functions for conversion of various values into strings that may be compared lexicographically.
 *
 */
public final class AmazonSimpleDBUtil {

    /**
     * static value hardcoding date format used for conversation of Date into String
     */
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final int LONG_DIGITS = 20;

    private AmazonSimpleDBUtil() {
    	/* utility class */
    }
    
    public static String encodeRealNumberRange(BigDecimal number, int maxNumDigits, BigDecimal offsetValue) {
        BigDecimal offsetNumber = number.add(offsetValue);
        String longString = offsetNumber.toString();
        int numZeroes = maxNumDigits - longString.length();
        StringBuilder strBuffer = new StringBuilder(numZeroes).append(longString.length());
        for (int i = 0; i < numZeroes; i++) {
            strBuffer.insert(i, '0');
        }
        strBuffer.append(longString);
        return strBuffer.toString();
    }

    public static String encodeRealNumberRange(BigDecimal number, int maxDigitsLeft, int maxDigitsRight, BigDecimal offsetValue) {
        BigDecimal shiftMultiplier = new BigDecimal(Math.pow(10, maxDigitsRight));
        BigDecimal shiftedNumber = number.multiply(shiftMultiplier);
        shiftedNumber = shiftedNumber.setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal shiftedOffset = offsetValue.multiply(shiftMultiplier);
        BigDecimal offsetNumber = shiftedNumber.add(shiftedOffset);
        String longString = offsetNumber.toString();
        int numBeforeDecimal = longString.length();
        int numZeroes = maxDigitsLeft + maxDigitsRight - numBeforeDecimal;
        StringBuilder strBuffer = new StringBuilder(numZeroes).append(longString.length());
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
        BigDecimal shiftMultiplier = new BigDecimal(Math.pow(10, maxDigitsRight));
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
        return result.substring(0, result.length() - 2) + ":" + result.substring(result.length() - 2);
    }

    /**
     * Decodes date value from the string representation created using encodeDate(..) function.
     *
     * @param	value	string representation of the date value
     * @return	original date value
     */
    public static Date decodeDate(String value) throws ParseException {
        String javaValue = value.substring(0, value.length() - 3) + value.substring(value.length() - 2);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.parse(javaValue);
    }

    /**
     * Encodes date value into a base64-encoded string.
     *
     * @return string representation of the date value
     */
    public static String encodeByteArray(byte[] byteArray) {
        return new String(Base64.encodeBase64(byteArray));
    }

    /**
     * Decodes byte[] value from the string representation created using encodeDate(..) function.
     *
     * @param value string representation of the date value
     * @return original byte[] value
     */
    public static byte[] decodeByteArray(String value) throws ParseException {
        return Base64.decodeBase64(value.getBytes());

    }
}
