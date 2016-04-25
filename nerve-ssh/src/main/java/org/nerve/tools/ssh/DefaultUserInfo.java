package org.nerve.tools.ssh;

import com.jcraft.jsch.UserInfo;

/**
 * com.zeus.ssh
 * Created by zengxm on 2015/12/9 0009.
 */
public class DefaultUserInfo implements UserInfo{
	public String getPassword(){ return passwd; }

	public DefaultUserInfo(String password)
	{
		this.passwd = password;
	}

	public boolean promptYesNo(String str){
		return true;
	}

	String passwd;

	public String getPassphrase(){ return null; }
	public boolean promptPassphrase(String message){ return true; }
	public boolean promptPassword(String message){
		return true;
	}
	public void showMessage(String message){
		System.out.println(message);
	}

	public String[] promptKeyboardInteractive(String destination,
	                                          String name,
	                                          String instruction,
	                                          String[] prompt,
	                                          boolean[] echo){
		return new String[3];
	}
}
