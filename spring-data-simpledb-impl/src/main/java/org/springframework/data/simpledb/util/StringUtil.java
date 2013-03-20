package org.springframework.data.simpledb.util;

public final class StringUtil {

	private StringUtil() {
		// utility class
	}

	public static String toLowerFirstChar(String source) {
		if(source == null) {
			return null;
		}

		if(source.length() == 1) {
			return source.toLowerCase();
		} else {
			String rest = source.substring(1);
			String start = String.valueOf(source.charAt(0));
			return start.toLowerCase() + rest;
		}
	}
	
	public static String removeExtraSpaces(final String source) {
		return source.replaceAll(" +", " ");
	}

}
