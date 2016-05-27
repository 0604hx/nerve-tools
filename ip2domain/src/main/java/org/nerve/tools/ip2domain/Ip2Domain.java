package org.nerve.tools.ip2domain;

import java.util.Set;

/**
 * Created by zengxm on 2016/5/25.
 */
public interface Ip2Domain {
	/**
	 * 根据ip尝试去查找匹配到的域名
	 * @param ip        需要解析的IP
	 * @return          可能与ip匹配的域名信息
	 */
	Set<String> lookup(String ip);

	/**
	 * 设置代理
	 * @param host      代理主机名
	 * @param port      代理端口
	 */
	void setProxy(String host, int port);
}
