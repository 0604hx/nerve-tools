package org.nerve.tools.ip2domain.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private String urlSelector="#b_results li h2 a";
	protected static Map<String,String> cookies=new HashMap<>();
	static {
		cookies.put("_EDGE_S","mkt=en-us&ui=en-us&F=1&SID=0F884792FDDB6BAB12F64EB3FC076AAA");
		cookies.put("_EDGE_V","1");
	}
	protected String searchUrl="http://global.bing.com/search?q=%s";

	@Override
	public Set<String> lookup(String ip) {
		if(urlSelector==null || urlSelector.length()==0)
			throw new IllegalArgumentException("urlSelector must be setup!see @setUrlSelector");
		Set<String> urls=new HashSet<>();
		try{
			getResponse(String.format(searchUrl, "ip:"+ip), response->{
				Document document=Jsoup.parse(response);
				Elements elements=document.select(urlSelector);
				elements.forEach(e-> urls.add(e.attr("href")+" "+e.text()));
			});

			//如果通过搜索关键字："ip:xxx.xx.x.x"无法得到结果，则直接搜索“xxx.xx.x.x"
			if(urls.size()==0){
				getResponse(String.format(searchUrl, ip), response->{
					Document document=Jsoup.parse(response);
					Elements elements=document.select(urlSelector);
					elements.forEach(e-> {
						String href=e.attr("href");
						if(!href.contains(ip))
							urls.add(e.text()+" "+href);
					});
				});
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return findDomainsFromUrls(urls);
	}

	public String getUrlSelector() {
		return urlSelector;
	}

	/**
	 * setup the url selector that location the results(html label A, for example:<a></a>).
	 *
	 * the default value is '#b_results li h2 a'.You can change it when bing search result page had been modified.
	 *
	 * @param urlSelector       selector similar to jQuery selector
	 * @return                  Ip2DomainByGlobalBingSearch
	 */
	public Ip2DomainByGlobalBingSearch setUrlSelector(String urlSelector) {
		this.urlSelector = urlSelector;
		return this;
	}

	@Override
	protected void onConnectionOpen(URLConnection connection) {
		StringBuilder sb=new StringBuilder();
		cookies.forEach((k,v)->sb.append(k+"="+v+"; "));
		String cookies=sb.toString().substring(0, sb.length()-2);
		connection.setRequestProperty("Cookie", cookies);
	}
}
