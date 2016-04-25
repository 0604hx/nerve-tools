package org.nerve.tools.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import org.nerve.tools.ssh.bean.RemoteBootEntity;
import org.nerve.tools.ssh.bean.SshInfo;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * com.zues.ssh
 * Created by zengxm on 2016/2/23 0023.
 */
public class SSHFactoryTest extends AbstractSshTest {

	@Test
	public void testRemoteBoot() throws IOException, InterruptedException, SftpException, JSchException {
		MessageBuilder mb=new MessageBuilder();
		try{
			SSHFactory.execRemoteBooeEntity(mb,getSshInfo131(), getSshInfo132(), getRemoteBoot(), 5);
		}catch (Exception e){
			mb.put("执行出错："+e.getMessage());
			e.printStackTrace();
		}

		for(String s:mb.getMessageList())
			System.out.println(s);
	}

	private RemoteBootEntity getRemoteBoot() throws FileNotFoundException {
		RemoteBootEntity rbe=new RemoteBootEntity();
		rbe.addBeforeCommand("jps");
		rbe.addBeforeCommand("pkill java");
		rbe.addFile("D:\\workspace\\intellij15\\jiepu\\jpea\\jpea-ssh\\src\\test\\resources\\thrift-server.jar");
		rbe.addFile("C:\\Users\\Administrator\\Desktop\\KeyTool.java");
		rbe.setReplace(true);
		rbe.setRemotePath("/usr/test2/jars");
		rbe.addAfterCommand("java -jar ${path}/thrift-server.jar 192.168.216.132");
		rbe.setCheckJava(true);

		return rbe;
	}

	private SshInfo getSshInfo131(){
		return new SshInfo( "192.168.216.131","root", "toor", 22);
	}
	private SshInfo getSshInfo132(){
		return new SshInfo( "192.168.216.132","root", "toor", 22);
	}
	private SshInfo getSshInfo103(){
		return new SshInfo( "192.168.1.103","root", "toor", 22);
	}

	@Test
	public void testExec() throws InterruptedException, JSchException, IOException {
		ExecHandler handler = new ExecHandler(getSshInfo132());
//		handler.exec("java -jar thrift-server.jar 192.168.216.132", 5000);
		handler.exec("cd /usr/test2",4000);
		handler.exec("pwd", 4000);
		System.out.println("-----"+handler.getResult());
		System.out.println(handler.getResultCode());
		System.out.println(handler.getResultError());
	}

	@Test
	public void testLocalPortForward() throws JSchException {
		//transit host
		SshInfo sshInfo=new SshInfo();
		sshInfo.setHost("192.168.1.1")
				.setPort(22)
				.setPassword("password")
				.setUser("root");

		PortForwardingHandler handler=new PortForwardingHandler(sshInfo);
		//监听10080端口，如果参数=0，则自动分配端口
		int localPort=handler.startLocalPortForwarding(10080, "8.8.8.8",22);
		//do something
		//....

		//stop local port listening
		handler.stopLocalPortForwarding(localPort);
		handler.disconnect();
	}

	@Test
	public void testLocalPortForward2() throws JSchException {
		//transit host
		SshInfo sshInfo=new SshInfo();
		sshInfo.setHost("192.168.1.1")
				.setPort(22)
				.setPassword("password")
				.setUser("root");

		//监听10080端口，如果参数=0，则自动分配端口
		int localPort = SSHFactory.openLocalPortToRemote(sshInfo, 10080,"8.8.8.8",22);
		//do something
		//....

		//stop local port listening
		SSHFactory.stopLocalPortForwarding(localPort);
	}

	public void testRemotePortForward() throws JSchException {
		//transit host
		SshInfo sshInfo=new SshInfo();
		sshInfo.setHost("192.168.1.1")
				.setPort(22)
				.setPassword("password")
				.setUser("root");

		PortForwardingHandler handler=new PortForwardingHandler(sshInfo);
		int remotePort=handler.startRemotePortForwarding(
				8080,               //中转机监听端口
				"localhost",        //本地IP
				9090                //本地提供服务的真实端口
		);
		//do something
		//....

		//stop local port listening
		handler.stopRemotePortForwarding(remotePort);
		handler.disconnect();
	}

	@Test
	public void testRemotePortForward2() throws JSchException, InterruptedException {
		//transit host
		SshInfo sshInfo=getSshInfo131();

		int remoatePort=SSHFactory.openRemotePortToLocal(sshInfo, 9090, 8080);
		//do something
		//....
		Thread.sleep(1000000);
	}
}
