package org.nerve.tools.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.nerve.tools.ssh.bean.PortLink;
import org.nerve.tools.ssh.bean.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

/**
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public abstract class AbstractSessionHandler {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected Session session;
	protected SshInfo sshInfo;
	protected PortLink portLink;

	protected AbstractSessionHandler(){}

	public AbstractSessionHandler(SshInfo sshInfo){
		this.sshInfo = sshInfo;
	}

	public AbstractSessionHandler(Session session){
		this.session = session;
	}


	/**
	 * 使用ssh连接到host
	 * @throws JSchException    if connect failed
	 */
	protected void connect() throws JSchException {
		if(checkSessionAvailable()){
			log.info("session is already connect to host({}@{}:{})!", session.getUserName(), session.getHost(), session.getPort());
			return ;
		}

		//尝试登陆
		JSch sch = new JSch();

		session = sch.getSession(sshInfo.getUser(), sshInfo.getHost(), sshInfo.getPort());
		session.setUserInfo(new DefaultUserInfo(sshInfo.getPassword()));
		Hashtable<String, String> config = new Hashtable<>();
		config.put("StrictHostKeyChecking", "no");
		config.put("userauth.gssapi-with-mic", "no");
		session.setDaemonThread(sshInfo.isDaemon());
		session.setConfig(config);
		session.connect();
	}

	/**
	 * 检查session是否可用
	 * @return true if session is usable
	 */
	public boolean checkSessionAvailable(){
		return session != null && session.isConnected() ;
	}

	public Session getSession() throws JSchException {
		if(session == null)
			connect();
		return session;
	}

	/**
	 * 判断是否已经连接
	 * @return true if the session is not null and connected
	 */
	public boolean isConnected(){
		return session == null?false:session.isConnected();
	}

	public void disconnect(){
		if(session != null)
			session.disconnect();
	}

	protected void logUnavailableSession(){
		log.error("session is not available! please check your connection information on {}@{}:{}",
				sshInfo.getUser(), sshInfo.getHost(), sshInfo.getPassword());
	}

	public PortLink getPortLink() {
		return portLink;
	}

	public AbstractSessionHandler setPortLink(PortLink portLink) {
		this.portLink = portLink;
		return this;
	}
}
