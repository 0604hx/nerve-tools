package org.nerve.tools.ip2domain.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * org.nerve.tools.ip2domain.impl
 * Created by zengxm on 2016/5/25.
 */
public abstract class AbstractHttpIp2Domain extends AbstractIp2Domain {
	protected String proxyHost;
	protected int proxyPort;
	protected int connectTimeout=30000;
	protected int readTimeout=10000;
	protected int connectSleepTime=1000;

	@Override
	public void setProxy(String host, int port) {
		this.proxyHost=host;
		this.proxyPort=port;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public long getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectSleepTime() {
		return connectSleepTime;
	}

	public AbstractHttpIp2Domain setConnectSleepTime(int connectSleepTime) {
		this.connectSleepTime = connectSleepTime;
		return this;
	}

	/**
	 *
	 * @param urlAddr           请求地址
	 * @return                  connection对象
	 * @throws IOException      for io failed
	 */
	protected URLConnection getConnection(String urlAddr) throws IOException {
		final URL url = new URL(urlAddr);
		if(proxyHost!=null && proxyHost.length()>0){
			//设置代理, 填写代理
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			System.out.println("use proxy="+proxyHost+":"+proxyPort);
			return url.openConnection(proxy);
		}else
			return url.openConnection();
	}

	/**
	 * 获取http结果
	 * @param url               请求地址
	 * @param consumer          后续的处理
	 * @throws IOException      for io failed
	 */
	protected void getResponse(String url,Consumer<String> consumer) throws IOException {
		final URLConnection connection = getConnection(url);
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		onConnectionOpen(connection);

		try {
			connection.connect();
			Thread.sleep(connectSleepTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String inputLine;
			final StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			consumer.accept(response.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param connection    创建的connection对象
	 */
	protected void onConnectionOpen(URLConnection connection){

	}
}
