package org.nerve.tools.fs.common;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * org.nerve.fs.common
 * Created by zengxm on 2015/10/8 0008.
 */
public final class HexUtils {
	/**
	 * Converts the given byte buffer to a hexadecimal string using {@link Integer#toHexString(int)}.
	 *
	 * @param bytes the bytes to convert to hex
	 * @return a String containing the hex representation of the given bytes.
	 */
	public final static String toHex(final byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (final byte b : bytes) {
			String s = Integer.toHexString(0xff & b);

			if (s.length() < 2) {
				sb.append("0");
			}
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * 获取指定流的md5
	 * @param is                输入流
	 * @return                  输入流的MD5
	 */
	public static String md5HexOfInputStream(InputStream is){
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			byte[] buffer = new byte[1024];

			int read = -1;
			while ((read = is.read(buffer, 0, 1024)) > -1) {
				md5.update(buffer, 0, read);
			}
			return toHex(md5.digest());
		}catch(Exception e){
			System.err.println("error on digest md5:"+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
