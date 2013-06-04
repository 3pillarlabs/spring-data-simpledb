package org.springframework.data.simpledb.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to compare Strings starting numbers and delimited with <tt>@</tt>.
 * <p/>
 * "10@attr" should be <b>bigger</b> than "2@attr" with this comparator
 */
public class AlphanumStringComparator implements Comparator<String> {

	private static Pattern prefixRexp = Pattern.compile("^(\\d+)@");

	@Override
	public int compare(String s1, String s2) {
		int c = 0;
		Matcher m1 = prefixRexp.matcher(s1);
		Matcher m2 = prefixRexp.matcher(s2);
		if (m1.find() && m2.find()) {
			Integer i1 = Integer.valueOf(m1.group(1));
			Integer i2 = Integer.valueOf(m2.group(1));
			c = i1.compareTo(i2);
		} else {
			throw new IllegalArgumentException("Can not compare strings missing 'digit@' pattern");
		}
		return c;
	}


}
