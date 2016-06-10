package org.nerve.tools.fs;

import com.esotericsoftware.reflectasm.FieldAccess;
import org.nerve.tools.fs.common.FilenameUtils;
import org.nerve.tools.fs.common.HexUtils;
import org.nerve.tools.fs.common.HostnameUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 分布式文件对象
 * org.nerve.fs
 * Created by zengxm on 2015/9/28 0028.
 */
public class DFile implements Serializable, Comparable<DFile>{
	public static final String OS=System.getProperty("os.name").toLowerCase();
	public static final boolean IS_WIN=OS.contains("win");

	public static final String ATTR_DIRECTORY = "directory";
	public static final String ATTR_MD5 = "md5";
	public static final String ATTR_PARENT = "parent";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_FILE_NAME = "filename";
	public static final String ATTR_PATH="path";
	public static final String ATTR_HOSTNAME="hostname";
	public static final String ATTR_SUFFIX="suffix";
	public static final String ATTR_OS="os";

	/**
	 * 统一的文件路径分隔符
	 */
	public static final String separator = "/";

	protected String filename;            //文件名
	protected long length;            //文件长度
	protected String parent;          //所属文件夹（路径）

	protected boolean directory;        //是否为文件夹，只有两中可能，文件或者目录
	protected boolean exist;            //指定的文件是否存在

	protected String md5;
	protected String suffix;            //（通常是后缀名）
	protected String path;              //文件全路径
	protected String hostname=HostnameUtils.NAME;//原文件的主机名
	protected String contentType;       //文件类型
	protected String author;            //作者
	protected Date createDate;          //创建时间
	protected Date modifyDate;          //最后修改时间
	protected Date accessDate;          //最后访问时间
	protected Date uploadDate;          //上传时间
	protected String remark;            //备注信息
	protected String os = OS;           //操作系统
	protected String category;              //分类
	protected Map<String,Object> metadata;  //元数据

	protected InputStream inputStream;

	/**
	 * 不存在的DFS文件
	 * @return
	 */
	public static final DFile noExist(){
		DFile f = new DFile();
		f.exist = false;
		return f;
	}

	public static DFile parse(final File file){
		DFile dFile=new DFile();
		if(file==null)
			return dFile;

		String path=FilenameUtils.toDFSPath(file.getAbsolutePath());
		dFile.setFilename(FilenameUtils.getName(path))
				.setPath(path)
				.setParent(FilenameUtils.getFullPathNoEndSeparator(path))
				.setSuffix(FilenameUtils.getExtension(path))
				.setDirectory(file.isDirectory())
				.setHostname(HostnameUtils.NAME)
		;

		if(file.exists()){
			javaxt.io.File xtFile=new javaxt.io.File(file.getAbsolutePath());
			dFile.setAccessDate(xtFile.getLastAccessTime())
					.setModifyDate(xtFile.getLastModifiedTime())
					.setCreateDate(xtFile.getCreationTime())
					.setContentType(xtFile.getContentType())
					.setLength(file.length())
			;

			//计算md5
			try{
				dFile.setMd5(HexUtils.md5HexOfInputStream(xtFile.getInputStream()));
			}catch (Exception e){
			}
		}

		return dFile;
	}

	public static DFile parse(String filePath) {
		DFile f = new DFile();
		String path = FilenameUtils.toDFSPath(filePath);
		f.filename = FilenameUtils.getName(path);
		f.parent = FilenameUtils.getFullPathNoEndSeparator(path);
		f.setPath(path);
		return f;
	}

	public DFile(){
	}

	/**
	 *
	 * @param os        操作系统
	 * @param is        输入流
	 */
	public DFile(String os,InputStream is){
		this.os=os;
		this.inputStream=is;
	}

	public DFile(boolean isDirectory, String parent, String name, long length, InputStream is){
		this.exist = true;
		this.directory = isDirectory;
		this.parent = parent;
		this.filename = name;
		this.length = length;
		this.inputStream = is;
	}

	public Map<String, Object> toMap(){
		if(getUploadDate()==null)
			setUploadDate(new Date());

		Map<String,Object> map=new HashMap<>();
		FieldAccess access=FieldAccess.get(this.getClass());
		for (String fn : access.getFieldNames()) {
//			if(!"exist".equals(fn))
			map.put(fn, access.get(this, fn));
		}
		map.remove("inputStream");  //不需要inputStream
		map.remove("exist");
		return map;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public long length(){
		return length;
	}

	/**
	 * 是否为一级目录
	 * @return
	 */
	public boolean isRoot(){
		return separator.equals(parent);
	}

	/**
	 * 转移目录
	 * @param targetDir
	 */
	public void moveToDir(String targetDir){
		String newP=targetDir+File.separator+filename;
		path=FilenameUtils.toDFSPath(newP);
		parent=FilenameUtils.getFullPathNoEndSeparator(path);
	}

	/**
	 * 判断是否为文件
	 * @return
	 */
	public boolean isFile(){
		return !directory;
	}

	/**
	 * 判断是否为文件夹
	 * @return
	 */
	public boolean isDirectory(){
		return directory;
	}

	public boolean exist(){
		return exist;
	}

	/**
	 * 获取文件的完整路径
	 * @return
	 */
	public String getAbsolutePath(){
		if(separator.equals(parent))
			return parent+filename;
		return parent+ separator + filename;
	}

	public String getParent(){
		return parent;
	}


	@Override
	public int compareTo(DFile o) {
		if(o == null)
			return -1;
		return this.getAbsolutePath().compareTo(o.getAbsolutePath());
	}

	public String getOs() {
		return os;
	}

	public String getCategory() {
		return category;
	}

	public DFile setCategory(String category) {
		this.category = category;
		return this;
	}

	public String getMd5() {
		return md5;
	}

	public DFile setMd5(String md5) {
		this.md5 = md5;
		return this;
	}

	public String getSuffix() {
		return suffix;
	}

	public DFile setSuffix(String suffix) {
		this.suffix = suffix==null?null:suffix.toUpperCase();
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public DFile setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public long getLength() {
		return length;
	}

	public DFile setLength(long length) {
		this.length = length;
		return this;
	}

	public DFile setParent(String parent) {
		this.parent = parent;
		return this;
	}

	public DFile setDirectory(boolean directory) {
		this.directory = directory;
		return this;
	}

	public boolean isExist() {
		return exist;
	}

	public DFile setExist(boolean exist) {
		this.exist = exist;
		return this;
	}

	public String getPath() {
		return path;
	}

	public DFile setPath(String path) {
		this.path = path;
		return this;
	}

	public String getHostname() {
		return hostname;
	}

	public DFile setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public DFile setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public DFile setAuthor(String author) {
		this.author = author;
		return this;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public DFile setCreateDate(Date createDate) {
		this.createDate = createDate;
		return this;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public DFile setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
		return this;
	}

	public Date getAccessDate() {
		return accessDate;
	}

	public DFile setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
		return this;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public DFile setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
		return this;
	}

	public String getRemark() {
		return remark;
	}

	public DFile setRemark(String remark) {
		this.remark = remark;
		return this;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public DFile setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}
}
