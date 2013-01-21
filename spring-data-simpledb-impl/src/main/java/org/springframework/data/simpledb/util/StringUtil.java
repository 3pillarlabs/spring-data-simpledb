package org.springframework.data.simpledb.util;

public final class StringUtil {

    private StringUtil(){
        //utility class
    }

    public static String[] splitCamelCaseString(String str) {
        return str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    public static String combineLowerCase(String[] values, String separator){
        StringBuffer buffer = new StringBuffer("");
        for (String value: values){
            buffer.append(value.toLowerCase());
            buffer.append(separator);
        }

        String str = buffer.toString();

        //remove last separator
        return str.substring(0, str.length()-1);
    }

}
