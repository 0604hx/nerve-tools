package org.nerve.tools.ip2domain;

import java.util.Set;

/**
 * Created by zengxm on 2016/5/25.
 */
public interface Ip2Domain {
	/**
	 * 根据ip尝试去查找匹配到的域名
	 * @param ip
	 * @return
	 */
	Set<String> lookup(String ip);

	/**
	 * 设置代理
	 * @param host
	 * @param port
	 */
	void setProxy(String host, int port);
}
