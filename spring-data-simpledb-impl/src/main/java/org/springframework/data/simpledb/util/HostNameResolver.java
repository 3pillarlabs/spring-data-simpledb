package org.springframework.data.simpledb.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class HostNameResolver {

	private HostNameResolver() {
		/* utility class */
	}
	
	public static String readHostname() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return "dev_" + address.getHostName().replaceAll("\\W+", "_");
		} catch(UnknownHostException e) {
			throw new IllegalArgumentException("Could not read host name", e);
		}
	}
}
