package org.nerve.tools.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.nerve.tools.ssh.bean.SshInfo;

/**
 * 用于处理端口转发的类
 * 功能如下：
 * 1.设置本地端口转发（参见SSH本地转发）
 * 2.设置远程端口转发（参见SSH远程转发）
 *
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public class PortForwardingHandler extends AbstractSessionHandler{

	public PortForwardingHandler(SshInfo sshInfo){
		super(sshInfo);
	}
	public PortForwardingHandler(Session session){
		super(session);
	}

	/**
	 * 开启本地端口映射
	 * 成功后，如果访问 localhost:{localPort} 那么，请求将通过SSH隧道转发到 remoteHost:remotePort
	 * @param localPort     本地端口
	 * @param remoteHost    远程主机
	 * @param remotePort    远程端口
	 * @return              打开的本地端口（如果成功的话）
	 * @throws JSchException    for operation failed
	 */
	public int startLocalPortForwarding(int localPort, String remoteHost, int remotePort) throws JSchException {
		Session s = getSession();
		if(checkSessionAvailable()){
			return s.setPortForwardingL(localPort, remoteHost, remotePort);
		}else
			logUnavailableSession();
		return -1;
	}

	/**
	 * 停止本地端口转发
	 * @param port      想要关闭的端口
	 * @throws JSchException    for operation failed
	 */
	public void stopLocalPortForwarding(int port) throws JSchException {
		if(port <=0){
			log.error("port is unavailable!");
			return;
		}
		log.info("try to stop LocalPort Forwarding on "+port);
		Session s = getSession();
		if(checkSessionAvailable()){
			s.delPortForwardingL(port);
			log.info("stop success!");
		}
		else
			logUnavailableSession();
	}

	/**
	 * 开启远程端口映射
	 * 成功后，在远程主机访问 localhost:remotePort 或者 127.0.0.1:remotePort 时，请求都会转发到 host:localhostPort
	 * @param remotePort        远程端口
	 * @param host              远程主机地址
	 * @param localPort         绑定的本地端口
	 * @throws JSchException    for operation failed
	 */
	public int startRemotePortForwarding(int remotePort, String host, int localPort) throws JSchException {
		Session s = getSession();
		if(checkSessionAvailable()) {
			s.setPortForwardingR(remotePort, host, localPort);
			return remotePort;
		}
		else
			logUnavailableSession();

		return -1;
	}

	/**
	 * 停止远程端口转发
	 * @param port      远程端口
	 * @throws JSchException    for operation failed
	 */
	public void stopRemotePortForwarding(int port) throws JSchException {
		if(port <=0){
			log.error("port is unavailable!");
			return;
		}

		Session s = getSession();
		if(checkSessionAvailable())
			s.delPortForwardingR(port);
		else
			logUnavailableSession();
	}
}
