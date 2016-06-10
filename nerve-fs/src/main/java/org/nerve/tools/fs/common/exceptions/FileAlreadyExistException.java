package org.nerve.tools.fs.common.exceptions;

/**
 * org.nerve.fs.common.exceptions
 * Created by zengxm on 2015/10/8 0008.
 */
public class FileAlreadyExistException extends RuntimeException {

	public FileAlreadyExistException(){
		super("Special File had exist， cannot upload again！");
	}

	public FileAlreadyExistException(String msg){
		super("Special File had exist， cannot upload again:"+msg);
	}
}