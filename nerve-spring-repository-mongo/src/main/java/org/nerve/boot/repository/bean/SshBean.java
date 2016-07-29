package org.nerve.boot.repository.bean;

import java.io.Serializable;

/**
 * SSH链接信息
 * Created by zengxm on 2015/12/9 0009.
 */
public class SshBean implements Serializable {
	public static final int DEFAULT_PORT = 22;

	private String host;
	private String user;
	private String password;
	private int port;

	public SshBean(){
		this.port = DEFAULT_PORT;
	}

	public SshBean(String host, String user, String password){
		this(host, user, password, DEFAULT_PORT);
	}

	public SshBean(String host, String user, String password, int port){
		this.setHost(host)
				.setUser(user)
				.setPassword(password)
				.setPort(port);
	}

	public String getHost() {
		return host;
	}

	public SshBean setHost(String host) {
		this.host = host;
		return this;
	}

	public String getUser() {
		return user;
	}

	public SshBean setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SshBean setPassword(String password) {
		this.password = password;
		return this;
	}

	public int getPort() {
		return port;
	}

	public SshBean setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public String toString() {
		return "SshBean{" +
				"host='" + host + '\'' +
				", user='" + user + '\'' +
				", password='" + password + '\'' +
				", port=" + port +
				'}';
	}
}
