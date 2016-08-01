package org.nerve.tools.ip2domain.impl;


import org.junit.Test;

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

		System.out.println(api.lookup("207.223.241.72"));
		System.out.println(api.lookup("125.39.240.113"));
		System.out.println(api.lookup("14.215.177.37"));
		System.out.println(api.lookup("180.97.164.26"));
		System.out.println(api.lookup("101.201.172.229"));
	}
}
