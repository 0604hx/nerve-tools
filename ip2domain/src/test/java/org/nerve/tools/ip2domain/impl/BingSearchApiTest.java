package org.nerve.tools.ip2domain.impl;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/25.
 */
public class BingSearchApiTest {

	@Test
	public void search(){
		Ip2DomainByBingSearchApi api=new Ip2DomainByBingSearchApi();
		//是否使用代理
		//api.setProxy("193.232.57.60",8090);
		api.setAccountKey("{{填写你的accountKey}}");

		System.out.println(api.lookup("125.39.240.113"));
		System.out.println(api.lookup("14.215.177.37"));
		System.out.println(api.lookup("180.97.164.26"));
		System.out.println(api.lookup("101.201.172.229"));
	}

	@Test
	public void searchWithGlobal(){
		Ip2DomainByGlobalBingSearch api=new Ip2DomainByGlobalBingSearch();

		//http://global.bing.com/search?q=ip:125.212.202.166&go=%E6%8F%90%E4%BA%A4&qs=n&pq=sa&sc=8-2&sp=-1&sk=&cvid=F453F64ACA5F44429CCB1D6E92A3A294&setmkt=en-us&setlang=en-us&FORM=SECNEN
		System.out.println(api.lookup("125.212.202.166"));
		System.out.println(api.lookup("125.39.240.113"));
		System.out.println(api.lookup("14.215.177.37"));
		System.out.println(api.lookup("180.97.164.26"));
		System.out.println(api.lookup("101.201.172.229"));
	}

	@Test
	public void getResultByJsoup() throws IOException {
		Document doc=Jsoup.connect("http://global.bing.com/search?q=ip:125.212.202.166&go=提交&qs=n&pq=sa&sc=8-2&sp=-1&sk=&cvid=F453F64ACA5F44429CCB1D6E92A3A294&setmkt=en-us&setlang=en-us&FORM=SECNEN&rdr=1&rdrig=89EDEE2E71234311A51B14D3B4C5CDCE").get();

		System.out.println(doc.body().html());
		Elements elements=doc.select("#b_results li h2 a");
		System.out.println("size="+elements.size());
		elements.forEach(element -> System.out.println(element.html()+" "+element.attr("href")));
	}
}
