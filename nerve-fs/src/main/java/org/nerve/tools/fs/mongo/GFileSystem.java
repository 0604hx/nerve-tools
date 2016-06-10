package org.nerve.tools.fs.mongo;

import static org.nerve.tools.fs.DFile.*;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;
import org.nerve.tools.fs.*;
import org.nerve.tools.fs.common.FilenameUtils;
import org.nerve.tools.fs.common.exceptions.FileAlreadyExistException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * 封装GridFS的工具类
 * org.nerve.fs.mongo
 * Created by zengxm on 2015/9/28 0028.
 */
public class GFileSystem<C extends FSConfig> extends AbstractDFileSystem<String> {
	protected static GridFS gridFS = null;

	private C config;

	private GFileSystem(){
	}
	private GFileSystem(C c){
		this.config = c;
	}

	@Override
	public void close() throws IOException {
	}

	/**
	 * 获取GFileSystem实例，
	 * @param db
	 * @return
	 */
	public static final GFileSystem getInstance(final DB db){
		FSConfig dConfig = new FSConfig();
		try{
			//加载配置文件
			Properties properties = new Properties();
			properties.load(GFileSystem.class.getResourceAsStream("/nerve-fs.properties"));

			dConfig.setNameSpace(properties.getProperty("nameSpace",dConfig.getNameSpace()));
			dConfig.setMaxFileSize(Long.valueOf(properties.getProperty(
							"maxFileSize",
							dConfig.getMaxFileSize()+"")
			));
			dConfig.setMd5Unique(Boolean.valueOf(properties.getProperty(
					"unique.md5"
			)));
		}catch(Exception e){
			System.err.println("failed to load nerve-fs.properties, use default values!");
		}

		if(gridFS == null){
			gridFS = new GridFS(db, dConfig.getNameSpace());
		}
		return new GFileSystem(dConfig);
	}

	public C getConfig() {
		return config;
	}
	public GFileSystem setConfig(C config) {
		this.config = config;
		return this;
	}

	protected void put(GridFSInputFile gif, DFile dFile){
		//设置统一的category
		dFile.setCategory(getCategory());

		Map<String,Object> map=dFile.toMap();
		map.forEach((k,v)->gif.put(k,v));
	}

	@Override
	public String upload(String filePath, Map<String, Object>... metadatas) throws IOException {
		return upload(new File(filePath), metadatas);
	}

	@Override
	public String upload(File file, String targetDir, Map<String, Object>... metadatas) throws IOException {
		if(file == null || !file.exists() || file.isDirectory())
			return null;

		GridFSInputFile gFile = gridFS.createFile(file);
		DFile dFile=DFile.parse(file);
		if(hasText(targetDir)){
			dFile.moveToDir(targetDir);
		}
		put(gFile, dFile);

		//检查文件是否存在
		if(isExist(dFile))
			throw new FileAlreadyExistException(dFile.getPath()+",  hostname="+dFile.getHostname());

		//判断是否有元数据
		if(metadatas!=null && metadatas.length>0){
			DBObject dbObject=new BasicDBObject();
			for (Map<String, Object> metadata : metadatas) {
				dbObject.putAll(metadata);
			}
			gFile.setMetaData(dbObject);
		}

		//是否判断md5重复
		if(config.isMd5Unique()){
			GridFSDBFile dbFile = gridFS.findOne(new BasicDBObject(ATTR_MD5, md5HexOfFile(file)));
			if(dbFile != null){
				throw new FileAlreadyExistException();
			}
		}

		//创建目录
		mkdirs(dFile.getParent());

		gFile.save();
		return gFile.getId().toString();
	}

	@Override
	public String upload(File file, Map<String, Object>... metadatas) throws IOException {
		return upload(file, null, metadatas);
	}

	@Override
	public boolean deleteById(String s) {
		try{
			gridFS.remove(new ObjectId(s));
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void uploadDir(File directory, boolean withChildren) throws IOException {
		if(directory == null || directory.isFile())
			return ;
		//创建目录
		mkdirs(directory.getAbsolutePath());
		File[] files = directory.listFiles();
		for(File f:files){
			if(f.isFile())
				upload(f);
			else if(f.isDirectory() && withChildren){
				uploadDir(f, withChildren);
			}
		}
	}

	@Override
	public boolean isExist(String path) {
		return gridFS.findOne(new BasicDBObject(ATTR_PATH, path))!=null;
	}

	@Override
	public boolean isExist(DFile dFile) {
		return dFile==null?
				false
				:
				gridFS.findOne(
					new BasicDBObject(ATTR_PATH, dFile.getPath())
						.append(ATTR_HOSTNAME, dFile.getHostname())
				)!=null;
	}

	protected long numberOfChildren(String path){
		return gridFS.getFileList(new BasicDBObject(ATTR_PARENT, FilenameUtils.toDFSPath(path))).count();
	}

	protected GridFSDBFile getDBFileByPath(String path){
		return gridFS.findOne(new BasicDBObject(ATTR_PATH, FilenameUtils.toDFSPath(path)));
	}

	@Override
	public DFile get(String path) {
		GridFSDBFile dbFile = getDBFileByPath(path);

		if(dbFile == null)
			return DFile.noExist();
		else {
			return convert(dbFile);
		}
	}

	@Override
	public DFile getById(String s) {
		GridFSDBFile dbFile=gridFS.findOne(new ObjectId(s));
		if(dbFile!=null)
			return convert(dbFile);
		return null;
	}

	protected DFile convert(GridFSDBFile dbFile){
		DFile f=new DFile((String) dbFile.get(ATTR_OS),dbFile.getInputStream());
		FieldAccess access=FieldAccess.get(DFile.class);
		for (String fn : access.getFieldNames()) {
			Object obj=dbFile.get(fn);
			if(obj!=null)
				access.set(f, fn ,dbFile.get(fn));
		}
		f.setExist(true);
		return f;
	}

	@Override
	public List<DFile> listFiles(String path) {
		List<GridFSDBFile> dbFiles = gridFS.find(new BasicDBObject(ATTR_PARENT, FilenameUtils.toDFSPath(path)), new BasicDBObject(ATTR_DIRECTORY,1));
		System.out.println(dbFiles.size()+"   on:"+FilenameUtils.toDFSPath(path));
		List<DFile> dFiles = new ArrayList<>();
		dbFiles.forEach(b->dFiles.add(convert(b)));
		return dFiles;
	}

	@Override
	public void search(DFileSearch search) {
		if(search==null)
			return;
		BasicDBObject searchObj=new BasicDBObject();
		//模糊查询filename
		if(hasText(search.getFilename())){
			searchObj.put(ATTR_FILE_NAME, Pattern.compile(search.getFilename()+"++", Pattern.CASE_INSENSITIVE));
		}
		if(hasText(search.getParent()))
			searchObj.put(ATTR_PARENT, search.getParent());
		if(hasText(search.getPath()))
			searchObj.put(ATTR_PATH, search.getPath());
		if(hasText(search.getSuffix()))
			searchObj.put(ATTR_SUFFIX, search.getSuffix());
		if(hasText(search.getHostname()))
			searchObj.put(ATTR_HOSTNAME, search.getHostname());

		//利用collection进行查询
		DBCollection collection=gridFS.getDB().getCollection(config.getNameSpace()+".files");
		if(collection!=null){
			DBCursor cursor=collection.find(searchObj);
			search.setTotal(cursor.count());
			cursor.skip((search.getPage()-1)*search.getPageSize()).limit(search.getPageSize());

			List<DFile> dFiles=new CopyOnWriteArrayList<>();
			while (cursor.hasNext()){
				DBObject dbObject=cursor.next();
				if(dbObject instanceof GridFSDBFile)
					dFiles.add(convert((GridFSDBFile)dbObject));
			}
			search.setdFiles(dFiles);
		}
	}

	@Override
	public boolean delete(File file) {
		return delete(file.getAbsoluteFile());
	}

	@Override
	public boolean delete(String filePath) {
		//判断是否存在
		DFile dFile = get(FilenameUtils.toDFSPath(filePath));
		if(dFile == null || !dFile.exist())
			return false;

		//判断是否为空的目录
		if(dFile.isDirectory() && numberOfChildren(dFile.getAbsolutePath())>0)
			return false;

		//删除操作
		gridFS.remove(new BasicDBObject(ATTR_PATH, FilenameUtils.toDFSPath(dFile.getPath())));
		return true;
	}

	@Override
	public boolean mkdir(String path) {
		return mkdir(DFile.parse(path));
	}

	@Override
	public boolean mkdirs(String path) {
		//如果存在，就不需要重复创建
		DFile dFile = DFile.parse(path);
		if(isExist(dFile.getAbsolutePath())){
			return false;
		}

		//尝试创建
		if(mkdir(dFile)){
			return true;
		}

		//到了这一步，则表示父目录不存在
		String parent = dFile.getParent();
		return (parent != null && !parent.equals(DFile.separator) && (mkdirs(parent) || isExist(parent)) &&
				mkdir(dFile));
	}

	/**
	 * 创建目录
	 * @param dFile         目录信息
	 */
	public boolean mkdir(DFile dFile){
		//先判断指定的目录是否存在
		if(isExist(dFile.getAbsolutePath()))
			return false;

		/*
		判断父目录是否存在， 如果父目录不存在，无法创建
		注意：
		如果DFile 位于一级目录（如 /log.txt, /abc)，则不判断
		 */
		if(!dFile.isRoot() && !isExist(dFile.getParent()))
			return false;

		GridFSInputFile gif = gridFS.createFile(new byte[]{});
		dFile.setDirectory(true);
		put(gif, dFile);

		gif.save();
		return true;
	}

	@Override
	public String write(String path, String fileContent) {
		GridFSDBFile dbFile = getDBFileByPath(path);
		GridFSInputFile gif = gridFS.createFile(fileContent==null?new byte[]{}:fileContent.getBytes());
		if(dbFile != null)
			gif.setId(dbFile.getId());

		DFile dFile = DFile.parse(path);
		return write(gif, dFile, false);
	}

	@Override
	public String write(String path, InputStream is) {
		GridFSDBFile dbFile = getDBFileByPath(path);
		GridFSInputFile gif = gridFS.createFile(is);
		if(dbFile != null)
			gif.setId(dbFile.getId());
		DFile dFile = DFile.parse(path);
		return write(gif, dFile,false);
	}

	protected String write(GridFSInputFile gif, DFile dFile, boolean removeOnExist){
		String fullPath = dFile.getParent();
		//创建目录
		mkdirs(fullPath);

		put(gif, dFile);
		gif.save();
		return gif.getId().toString();
	}
}
