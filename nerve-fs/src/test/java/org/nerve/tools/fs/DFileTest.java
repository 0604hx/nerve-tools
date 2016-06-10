package org.nerve.tools.fs;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.nerve.tools.fs.common.FilenameUtils;

/**
 * org.nerve.tools.fs
 * Created by zengxm on 2016/6/7.
 */
public class DFileTest {

	@Test
	public void toMap(){
		DFile dFile=DFile.parse("D:\\numbers.txt");

		System.out.println(dFile.toMap());
	}

	@Test
	public void path(){
		DFile file=DFile.parse("D:");
		System.out.println(JSON.toJSONString(file,true));

		System.out.println(FilenameUtils.getPathNoEndSeparator("/D:/abc/ad/2"));
	}
}
