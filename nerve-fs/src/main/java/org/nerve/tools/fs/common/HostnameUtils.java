package org.nerve.tools.fs.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * org.nerve.tools.fs.common
 * Created by zengxm on 2016/6/7.
 */
public final class HostnameUtils {
	public static String NAME=null;

	public static String getHostNameForLiunx() {
		try {
			return (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException uhe) {
			String host = uhe.getMessage(); // host = "hostname: hostname"
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					return host.substring(0, colon);
				}
			}
			return "UnknownHost";
		}
	}


	public static String getHostName() {
		if (System.getenv("COMPUTERNAME") != null) {
			return System.getenv("COMPUTERNAME");
		} else {
			return getHostNameForLiunx();
		}
	}

	static {
		NAME=getHostName();
	}
}
