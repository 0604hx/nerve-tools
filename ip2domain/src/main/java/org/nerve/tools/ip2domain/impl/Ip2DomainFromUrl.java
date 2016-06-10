package org.nerve.tools.ip2domain.impl;

import org.nerve.tools.ip2domain.DirUtil;

import java.nio.file.Files;
import java.util.*;
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
		initUrlRegs();
	}

	protected void initUrlRegs(){
		urlRegs=new ArrayList<>();
		urlRegs.add("http://www.seoreporttools.com/stats/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://www.ipaddresslocation.org/ip-address-locator.php?lookup=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://whoissoft.com/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://www.ip-tracker.org/blacklist-check.php?ip=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("https://www.robtex.net/?_escaped_fragment_=dns=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("https://lzone.de/websites/history/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("https://www.robtex.org/#!dns=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://www.domaincrawler.com/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("https://www.robtex.com/#!dns=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://mostpopularwebsites.net/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://ip.911cha.com/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2}).html");
		urlRegs.add("http://webvaluecheck.com/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://hao.yaozui.com/review/([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
		urlRegs.add("http://www.114best.com/ip/114.aspx?w=([a-z0-9-]+\\.[a-z0-9-]+(\\.[a-z0-9-]+){0,2})");
	}

	/**
	 * 增加新的url解析正则
	 * @param rs    用于抽取domain的正则表达式
	 */
	public void addUrlRegs(String... rs){
		urlRegs.addAll(Arrays.asList(rs));
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
