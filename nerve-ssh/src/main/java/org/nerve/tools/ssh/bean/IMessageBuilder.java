package org.nerve.tools.ssh.bean;

import java.util.List;

/**
 * com.zeus.ssh.bean
 * Created by zengxm on 2016/2/23 0023.
 */
public interface IMessageBuilder {
	IMessageBuilder put(String msg, Object... objs);
	String getLast();
	List<String> getMessageList();
}
