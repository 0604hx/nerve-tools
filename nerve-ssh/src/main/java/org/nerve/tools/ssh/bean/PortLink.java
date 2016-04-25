package org.nerve.tools.ssh.bean;

import java.io.Serializable;

/**
 * 端口关联
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public class PortLink implements Serializable {

	private String host;
	private int port;
	private String remoteHost;
	private int remotePort;
	private Object data;

	public PortLink(){}
	public PortLink(int p, String h2, int p2){
		this.setHost("localhost")
				.setPort(p)
				.setRemoteHost(h2)
				.setRemotePort(p2);
	}
	public PortLink(String h, int p, String h2, int p2){
		this.setHost(h)
				.setPort(p)
				.setRemoteHost(h2)
				.setRemotePort(p2);
	}

	public String getHost() {
		return host;
	}

	public PortLink setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public PortLink setPort(int port) {
		this.port = port;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public PortLink setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public PortLink setRemotePort(int remotePort) {
		this.remotePort = remotePort;
		return this;
	}

	public Object getData() {
		return data;
	}

	public PortLink setData(Object data) {
		this.data = data;
		return this;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
