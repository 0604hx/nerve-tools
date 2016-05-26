package org.nerve.tools.ip2domain;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by zengxm on 2016/4/20 0020.
 */
public class DirUtil {

	public static final String PATH;

	static {
		URL url = DirUtil.class.getProtectionDomain().getCodeSource().getLocation();

		String strRealPath = url.getPath();
		try {
			strRealPath = URLDecoder.decode(strRealPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (strRealPath.endsWith(".jar")) {
			strRealPath = strRealPath.substring(0, strRealPath.lastIndexOf("/") + 1);
		}

		File objFile = new File(strRealPath);
		PATH=objFile.getAbsolutePath();
	}

	public static String get(String location){
		return PATH+location;
	}
	public static Path getPath(String location){
		return Paths.get(PATH, location);
	}
}
