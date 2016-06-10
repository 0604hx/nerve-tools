package org.nerve.tools.fs;

import java.io.Serializable;

/**
 * org.nerve.tools.fs
 * Created by zengxm on 2016/6/7.
 */
public class FSConfig implements Serializable{
	private String nameSpace = "fs";
	private long maxFileSize = 1024*1024*1024;       //最大1GB
	private boolean md5Unique;

	public String getNameSpace() {
		return nameSpace;
	}

	public FSConfig setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
		return this;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public FSConfig setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}

	public boolean isMd5Unique() {
		return md5Unique;
	}

	public FSConfig setMd5Unique(boolean md5Unique) {
		this.md5Unique = md5Unique;
		return this;
	}
}
