package org.springframework.data.simpledb.util;

public final class StringUtil {

    private StringUtil() {
        //utility class
    }

    public static String[] splitCamelCaseString(String str) {
        return str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    public static String combineLowerCase(String[] values, String separator) {
        StringBuilder buffer = new StringBuilder("");
        for (String value : values) {
            buffer.append(value.toLowerCase());
            buffer.append(separator);
        }

        String str = buffer.toString();

        //remove last separator
        return str.substring(0, str.length() - 1);
    }

    public static String toLowerFirstChar(String source) {
        if (source == null) {
            return null;
        }

        if (source.length() == 1) {
            return source.toLowerCase();
        } else {
            String rest = source.substring(1);
            String start = String.valueOf(source.charAt(0));
            return start.toLowerCase() + rest;
        }
    }
}
