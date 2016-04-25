package org.nerve.tools.ssh.bean;

import java.io.Serializable;

/**
 * SSH链接信息
 * com.zeus.ssh
 * Created by zengxm on 2015/12/9 0009.
 */
public class SshInfo implements Serializable {
	public static final int DEFAULT_PORT = 22;

	private String host;
	private String user;
	private String password;
	private int port;
	private boolean daemon;

	public SshInfo(){
		this.port = DEFAULT_PORT;
	}

	public SshInfo(String host, String user, String password){
		this(host, user, password, DEFAULT_PORT);
	}

	public SshInfo(String host, String user, String password, int port){
		this.setHost(host)
				.setUser(user)
				.setPassword(password)
				.setPort(port);
	}

	public boolean isDaemon() {
		return daemon;
	}

	public SshInfo setDaemon(boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	public String getHost() {
		return host;
	}

	public SshInfo setHost(String host) {
		this.host = host;
		return this;
	}

	public String getUser() {
		return user;
	}

	public SshInfo setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SshInfo setPassword(String password) {
		this.password = password;
		return this;
	}

	public int getPort() {
		return port;
	}

	public SshInfo setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public String toString() {
		return "SshInfo{" +
				"daemon=" + daemon +
				", host='" + host + '\'' +
				", user='" + user + '\'' +
				", password='" + password + '\'' +
				", port=" + port +
				'}';
	}
}
