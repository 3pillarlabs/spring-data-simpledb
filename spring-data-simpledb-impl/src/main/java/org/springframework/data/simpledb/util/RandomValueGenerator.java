package org.springframework.data.simpledb.util;

public final class RandomValueGenerator {

	private RandomValueGenerator() {
		// utility class
	}

	public static String generateStringOfLength(int length) {
		final StringBuilder sb = new StringBuilder();
		while(sb.length() < length) {
			sb.append("a");
		}
		return sb.toString();
	}

	public static long[] generateArrayOfSize(int size) {
		final long[] longPrimitiveArray = new long[size];
		for(int i = 0; i < size; i++) {
			longPrimitiveArray[i] = i;
		}
		return longPrimitiveArray;
	}

}
