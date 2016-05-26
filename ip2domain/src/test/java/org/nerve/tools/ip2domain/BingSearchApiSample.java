package org.nerve.tools.ip2domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.net.SocksProxy;

public class BingSearchApiSample {

	public static void main(final String[] args) throws Exception {
		final String accountKey = "PPB5zLJ7+6jfGEFOTfF34JNihJAZQO7fRt88H/lMU74";
		final String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=JSON";

//		final String query = URLEncoder.encode("'125.39.240.113'", Charset.defaultCharset().name());
		final String bingUrl = String.format(bingUrlPattern, "125.39.240.113");
		System.out.println(bingUrl);
		final String accountKeyEnc = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());

		//设置代理, 填写代理
//		System.setProperty("proxySet", "true");
//		System.setProperty("proxyHost", "118.97.27.82");
//		System.setProperty("proxyPort", "8080");

		SocketAddress addr = new InetSocketAddress("118.97.27.82", 8080);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

		final URL url = new URL(bingUrl);
		final URLConnection connection = url.openConnection(proxy);
		connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

		try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String inputLine;
			final StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			final JSONObject json = new JSONObject(response.toString());
			final JSONObject d = json.getJSONObject("d");
			final JSONArray results = d.getJSONArray("results");
			final int resultsLength = results.length();
			for (int i = 0; i < resultsLength; i++) {
				final JSONObject aResult = results.getJSONObject(i);
//				System.out.println(aResult.toString());
				System.out.println(aResult.get("Url"));
			}
		}
	}

}