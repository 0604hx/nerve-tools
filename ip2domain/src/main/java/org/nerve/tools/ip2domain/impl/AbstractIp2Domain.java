package org.nerve.tools.ip2domain.impl;

import org.nerve.tools.ip2domain.Ip2Domain;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/25.
 */
public abstract class AbstractIp2Domain implements Ip2Domain {
	//识别IP地址的正则表达式
	protected String ipReg="\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
	protected boolean removeIP=false;


	public String getIpReg() {
		return ipReg;
	}
	public void setIpReg(String ipReg) {
		this.ipReg = ipReg;
	}

	public boolean isRemoveIP() {
		return removeIP;
	}

	public void setRemoveIP(boolean removeIP) {
		this.removeIP = removeIP;
	}

	/**
	 * @param domains   域名结果集合
	 * @return          处理后的结果（根据removeIP来决定是否去除IP）
	 */
	protected Set<String> dealWithResults(Set<String> domains){
		if(removeIP){
			Pattern p=Pattern.compile(ipReg);
			Set<String> noIpDomains=new HashSet<>();
			domains.stream().filter(domain-> !p.matcher(domain).find()).forEach(domain->noIpDomains.add(domain));
			return noIpDomains;
		}

		return domains;
	}
}
