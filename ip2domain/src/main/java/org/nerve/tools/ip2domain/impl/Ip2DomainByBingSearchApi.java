package org.nerve.tools.ip2domain.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * 使用bing search api 进行查询，需要VPN的支持（大陆网络得不到结果）
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/25.
 */
public class Ip2DomainByBingSearchApi extends Ip2DomainFromUrl {
	protected String accountKey;

	public Ip2DomainByBingSearchApi setAccountKey(String accountKey) {
		this.accountKey = accountKey;
		return this;
	}

	@Override
	public Set<String> lookup(String ip) {
		final String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=JSON";

		final String bingUrl = String.format(bingUrlPattern, ip);

		Set<String> urls=new HashSet<>();
		try{
			getResponse(bingUrl, response->{
				final JSONObject json = new JSONObject(response);
				final JSONObject d = json.getJSONObject("d");
				final JSONArray results = d.getJSONArray("results");
				final int resultsLength = results.length();
				for (int i = 0; i < resultsLength; i++) {
					final JSONObject aResult = results.getJSONObject(i);

					String resultUrl=aResult.getString("Url");
					urls.add(resultUrl);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

		return findDomainsFromUrls(urls);
	}

	@Override
	protected void onConnectionOpen(URLConnection connection) {
		final String accountKeyEnc = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());
		connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
	}
}
