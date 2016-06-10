package org.nerve.tools.fs;

import java.io.Serializable;
import java.util.List;

/**
 * org.nerve.tools.fs
 * Created by zengxm on 2016/6/7.
 */
public class DFileSearch implements Serializable{
	private List<DFile> dFiles;
	private String filename;    //根据文件名进行搜索（模糊）
	private String path;        //根据path（全路径）进行搜索（精确）
	private String parent;      //根据parent进行搜索（精确）
	private String suffix;      //根据后缀进行搜索（精确）
	private String hostname;    //根据主机名查询（精确）

	private int page;
	private int pageSize;
	private long total;

	public String getHostname() {
		return hostname;
	}

	public DFileSearch setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public List<DFile> getdFiles() {
		return dFiles;
	}

	public DFileSearch setdFiles(List<DFile> dFiles) {
		this.dFiles = dFiles;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public DFileSearch setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public String getPath() {
		return path;
	}

	public DFileSearch setPath(String path) {
		this.path = path;
		return this;
	}

	public String getParent() {
		return parent;
	}

	public DFileSearch setParent(String parent) {
		this.parent = parent;
		return this;
	}

	public String getSuffix() {
		return suffix;
	}

	public DFileSearch setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public int getPage() {
		return page;
	}

	public DFileSearch setPage(int page) {
		this.page = page;
		return this;
	}

	public int getPageSize() {
		return pageSize;
	}

	public DFileSearch setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public long getTotal() {
		return total;
	}

	public DFileSearch setTotal(long total) {
		this.total = total;
		return this;
	}
}
