package org.nerve.tools.fs;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.nerve.tools.fs.common.FilenameUtils;
import org.nerve.tools.fs.mongo.GFileSystem;

import java.io.File;
import java.io.IOException;

/**
 * org.nerve.tools.fs
 * Created by zengxm on 2016/6/7.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GFileSystemTest {

	DFileSystem<String> fs;
	MongoClient client;

	String uuid;
	String filePath="D:/numbers.txt";

	@SuppressWarnings("deprecation")
	@Before
	public void init(){
		client=new MongoClient("localhost", 27017);
		fs=GFileSystem.getInstance(client.getDB("mongos"));
	}

	@After
	public void destory(){
		//fs.deleteById(uuid);
		if(client!=null)
			client.close();
	}

	@Test
	public void addFile() throws IOException {
		uuid=fs.upload(filePath);

		String dfsPath=FilenameUtils.toDFSPath(filePath);
		DFile dFile=fs.getById(uuid);

		Assert.assertNotNull(dFile);
		Assert.assertEquals(dfsPath, dFile.getPath());
	}

	@Test
	public void addFileToDir()throws IOException{
		String dir="/mongo/192.168.1.1";
		uuid=fs.upload(new File(filePath), dir);

		DFile dFile=fs.getById(uuid);

		Assert.assertNotNull(dFile);
		Assert.assertEquals(dir+"/"+dFile.filename, dFile.getPath());
	}

	@Test
	public void write(){
		fs.setCategory("TEST");
		String path="/mongo/ips.txt";
		uuid=fs.write(path,"我们都是好孩子");

		DFile dFile=fs.getById(uuid);
		Assert.assertNotNull(dFile);
		Assert.assertEquals(FilenameUtils.toDFSPath(path), dFile.getPath());
	}

	@Test
	public void search(){
		DFileSearch search=new DFileSearch();
		search.setPath(FilenameUtils.toDFSPath(filePath));
		fs.search(search);

		search.getdFiles().forEach(d-> System.out.println(d.path));

		Assert.assertEquals(1, search.getTotal());
	}

	@Test
	public void get(){
		DFile dFile=fs.getById(uuid);
		System.out.println(JSON.toJSONString(dFile, true));
		Assert.assertNotNull(dFile);

		Assert.assertEquals("TXT", dFile.suffix);
	}

	@Test
	public void mkdirs(){
		fs.mkdirs("D:/txts");
	}

	@Test
	public void delete(){
		fs.delete("/mongo/182.54.199.107/test/_Audience.json");
	}
}
