package org.nerve.tools.fs.common;

/**
 * org.nerve.fs.common
 * Created by zengxm on 2015/10/8 0008.
 */
public class FilenameUtils extends org.apache.commons.io.FilenameUtils {

	/**
	 * 将文件路径转换为dfs统一路径
	 * dfs使用的是UNIX格式的路径，如 /a/b/c.txt
	 * 在window下，上述路径可能是： a:\b\c.txt
	 *
	 * @param filePath      文件原始路径
	 * @return              dfs的统一路径
	 */
	public static String toDFSPath(String filePath){
		if(filePath == null)
			return null;
		filePath = separatorsToUnix(filePath);
		return filePath.indexOf(47)==0?filePath:'/'+filePath;
	}
}
