package org.nerve.tools.ip2domain.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 使用global.bing.com来进行查询
 * 不需要代理
 *
 * 示例：http://global.bing.com/search?q=ip%3A14.215.177.37
 *
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/25.
 */
public class Ip2DomainByGlobalBingSearch extends Ip2DomainFromUrl {
	protected static Map<String,String> cookies=new HashMap<>();
	static {
		cookies.put("_EDGE_S","mkt=en-us&ui=en-us&F=1&SID=0F884792FDDB6BAB12F64EB3FC076AAA");
		cookies.put("_EDGE_V","1");
	}
	protected String searchUrl="http://global.bing.com/search?q=ip:%s";

	@Override
	public Set<String> lookup(String ip) {
		Set<String> urls=new HashSet<>();
		try{
			getResponse(String.format(searchUrl, ip), response->{
				Document document=Jsoup.parse(response);
				Elements elements=document.select("#b_results li h2 a");
				elements.forEach(e-> urls.add(e.attr("href")+" "+e.text()));
			});
		}catch (Exception e){
			e.printStackTrace();
		}
		return findDomainsFromUrls(urls);
	}

	@Override
	protected void onConnectionOpen(URLConnection connection) {
		StringBuilder sb=new StringBuilder();
		cookies.forEach((k,v)->sb.append(k+"="+v+"; "));
		String cookies=sb.toString().substring(0, sb.length()-2);
		connection.setRequestProperty("Cookie", cookies);
	}
}
