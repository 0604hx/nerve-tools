package org.nerve.tools.fs;

import java.io.Serializable;

/**
 * org.nerve.tools.fs
 * Created by zengxm on 2016/6/9.
 */
public abstract class AbstractDFileSystem<ID extends Serializable> implements DFileSystem<ID>{
	protected static String CATEGORY=null;

	@Override
	public void setCategory(String category) {
		AbstractDFileSystem.CATEGORY = category;
	}

	public String getCategory(){
		return CATEGORY;
	}
}
