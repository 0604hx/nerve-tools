package org.nerve.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * org.nerve.webmagic.utils
 * Created by zengxm on 2016/4/20 0020.
 */
public class PathUtils {

	public static final String PATH_JAR;

	static {
		URL url = PathUtils.class.getProtectionDomain().getCodeSource().getLocation();

		String strRealPath = url.getPath();
		try {
			strRealPath = URLDecoder.decode(strRealPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (strRealPath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
			// 截取路径中的jar包名
			strRealPath = strRealPath.substring(0, strRealPath.lastIndexOf("/") + 1);
		}

		File objFile = new File(strRealPath);
		PATH_JAR=objFile.getAbsolutePath();
	}

	/**
	 * 获取资源路径
	 * @return  路径
	 */
	public static String getResourcePath(){
		try{
			return Paths.get(PathUtils.class.getClassLoader().getResource("").toURI()).toString();
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public static String getPathRelativeToJar(String location){
		return PATH_JAR+location;
	}
	public static Path pathRelativeToJar(String location){
		return Paths.get(PATH_JAR, location);
	}
}
