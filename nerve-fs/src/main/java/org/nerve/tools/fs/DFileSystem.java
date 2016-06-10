package org.nerve.tools.fs;

import org.nerve.tools.fs.common.HexUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 分布式文件系统接口
 * Created by zengxm on 2015/9/28 0028.
 */
public interface DFileSystem<ID extends Serializable> extends Closeable{

	/**
	 * 设置文件分类，每次上传的文件&目录都会标记成此分类
	 * @param category
	 */
	void setCategory(String category);

	/**
	 * 将本地的一个文件放入分布式文件系统中
	 * @param filePath          文件的全路径
	 * @param metadatas         额外的元数据
	 * @return                  UUID
	 * @throws IOException
	 */
	ID upload(String filePath, Map<String,Object>... metadatas)throws IOException;

	/**
	 * 将本地的一个文件放入分布式文件系统中
	 * @param file          文件的全路径
	 * @param metadatas         额外的元数据
	 * @return                  UUID
	 * @throws IOException
	 */
	ID upload(File file, Map<String,Object>... metadatas)throws IOException;

	/**
	 * 上传本地文件到DFS指定的目录中
	 * @param file              文件对象
	 * @param targetDir         DFS的目录，如/usr/local
	 * @param metadatas         额外的元数据
	 * @return
	 * @throws IOException
	 */
	ID upload(File file, String targetDir, Map<String,Object>... metadatas)throws IOException;

	/**
	 * 上传整个文件夹到DFS中
	 * @param directory     文件夹
	 * @param withChildren  是否遍历子文件夹
	 */
	void uploadDir(File directory, boolean withChildren) throws IOException;

	/**
	 * 判断指定路径的文件是否存在
	 * @param path      路径（全路径）
	 * @return          如果指定的路径存在，则返回true，反之false
	 */
	boolean isExist(String path);

	/**
	 * 判断是否存在
	 * @param dFile     判断对象
	 * @return          如果指定的路径存在，则返回true，反之false
	 */
	boolean isExist(DFile dFile);

	/**
	 * 获取指定路径的文件信息
	 * @param path      全路径
	 * @return          DFile对象
	 */
	DFile get(String path);

	/**
	 * 根据ID获取文件
	 * @param id        UUID
	 * @return          DFile if exist
	 */
	DFile getById(ID id);

	List<DFile> listFiles(String path);

	/**
	 * 简单搜索
	 * @param search        搜索对象
	 */
	void search(DFileSearch search);

	/**
	 * 删除文件
	 * 注意：如果删除的是一个不为空的目录，将无法删除
	 * @param file          文件对象
	 */
	boolean delete(File file);

	boolean delete(String filePath);

	/**
	 * 根据UUID来删除文件
	 * @param id        UUID
	 * @return          true is delete success, false for failed
	 */
	boolean deleteById(ID id);

	/**
	 * 创建目录
	 * @param path      目录路径
	 * @return          成功返回true，反之false
	 */
	boolean mkdir(String path);

	/**
	 * 创建目录（包含父目录）
	 * @param path      目录路径
	 * @return          成功返回true，反之false
	 */
	boolean mkdirs(String path);


	/**
	 * 保存指定的字符串到path对应的文件中
	 *
	 * 注意：如果path对应的文件不存在，则会自动创建
	 * 如果存在，则报错
	 * @param path              文件路径
	 * @param fileContent       文件内容
	 * @return
	 */
	ID write(String path, String fileContent);

	/**
	 * 将is写入指定文件中
	 * 如果文件不存在则创建，存在的话，报错
	 * @param path              文件路径
	 * @param is                输入流
	 * @return
	 */
	ID write(String path, InputStream is);


	/**
	 * 获取文件的md5
	 * @param file              文件对象
	 * @return                  指定文件的md5值
	 * @throws FileNotFoundException
	 */
	default String md5HexOfFile(File file) throws FileNotFoundException {
		return HexUtils.md5HexOfInputStream(new FileInputStream(file));
	}

	default boolean hasText(String str){
		return str!=null && str.trim().length()>0;
	}
}
