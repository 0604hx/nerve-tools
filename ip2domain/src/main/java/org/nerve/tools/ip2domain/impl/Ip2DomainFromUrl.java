package org.nerve.tools.ip2domain.impl;

import org.nerve.tools.ip2domain.DirUtil;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从网络上获取到相关的url（可能包含指定ip的域名）
 * 然后从这些url中抽取域名的辅助类
 *
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/26.
 */
public abstract class Ip2DomainFromUrl extends AbstractHttpIp2Domain {

	protected List<String> urlRegs;

	public Ip2DomainFromUrl() {
		try{
			urlRegs= Files.readAllLines(DirUtil.getPath("domain-regs.txt"));
			System.out.println(urlRegs.size()+" regulars loaded!");
		}catch (Exception e){
			System.err.println("unable load domain-regs.txt. Please create it!");
		}
	}

	/**
	 * 使用正则表达式匹配域名
	 * @param urls      包含了搜索结果的地址集合
	 * @return          处理后的结果
	 */
	protected Set<String> findDomainsFromUrls(Set<String> urls){
		Set<String> domains=new HashSet<>();
		urls.forEach(u->urlRegs.forEach(r->{
			Pattern p=Pattern.compile(r);
			Matcher m=p.matcher(u);
			while(m.find()){
				if(m.groupCount()>0)
					domains.add(m.group(1));
			}
		}));

		return dealWithResults(domains.isEmpty()?urls:domains);
	}
}
