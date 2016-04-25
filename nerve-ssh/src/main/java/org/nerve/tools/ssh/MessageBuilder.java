package org.nerve.tools.ssh;

import org.nerve.tools.ssh.bean.IMessageBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * com.zeus.ssh
 * Created by zengxm on 2016/2/23 0023.
 */
public class MessageBuilder implements IMessageBuilder {
	public static final String F="yyyy-MM-dd HH:mm:ss.SSS ";

	private List<String> messages;
	private SimpleDateFormat sdf;
	private String lastMsg;

	public MessageBuilder(){
		this.messages=new ArrayList<>();
		sdf=new SimpleDateFormat(F);
	}
	public MessageBuilder(String msg){
		this();

	}

	public synchronized MessageBuilder put(String msg, Object ... objs){
		if(msg==null)
			return this;

		String dateT=sdf.format(new Date());
		lastMsg = (objs.length==0?msg:String.format(msg, objs));
		messages.add(dateT+lastMsg);

		return this;
	}

	@Override
	public String getLast(){
		return lastMsg;
	}

	@Override
	public List<String> getMessageList(){
		return messages;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(String m:messages)
			sb.append(m+"\n");
		return sb.toString();
	}
}
