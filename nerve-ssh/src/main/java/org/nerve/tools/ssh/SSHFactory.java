package org.nerve.tools.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.nerve.tools.ssh.bean.IMessageBuilder;
import org.nerve.tools.ssh.bean.PortLink;
import org.nerve.tools.ssh.bean.RemoteBootEntity;
import org.nerve.tools.ssh.bean.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于保存&初始化SSH通讯隧道
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public final class SSHFactory {
	private static Logger log = LoggerFactory.getLogger(SSHFactory.class);

	private static Map<Integer, PortForwardingHandler> portForwardingMap = new ConcurrentHashMap<>();

	/**
	 * 开通远程到本地的端口映射
	 * 注意，使用此功能需要占用远程主机的两个端口
	 * 端口1：使用SSH的远程端口转发，将端口1跟本地localPort关联在一起，这时，在远程主机访问 localhost:端口1
	 *          的请求都转发到本地
	 * 端口2：经过上一步后，端口1只能在远程主机内部使用，外部的主机都不能使用端口1，这时需要在远程主机中开通
	 *          全局的SSH本地端口转发（ssh -g -L端口2:localhost:端口1 localhost）
	 *
	 * 注意：
	 *  1.默认情况下，端口2 = remotePort，端口1 = remotePort - 1;
	 *  2.如果需要自己指定两个端口，请使用 @openRemotePortToLocal(SshInfo,int, int, int)
	 * @param remoteSshInfo
	 * @param localPort
	 * @return
	 */
	public static int openRemotePortToLocal(
			SshInfo remoteSshInfo,
			int remotePort,
			int localPort) throws JSchException {
		return openRemotePortToLocal(remoteSshInfo, remotePort-1,remotePort, localPort);
	}

	/**
	 * @param remoteSshInfo
	 * @param remotePort1
	 * @param remotePort2
	 * @param localPort
	 * @return
	 * @throws JSchException
	 */
	public synchronized static int openRemotePortToLocal(
			SshInfo remoteSshInfo,
			int remotePort1,
			int remotePort2,
			int localPort) throws JSchException {
		if(portForwardingMap.containsKey(localPort)){
			throw new SSHException("local port "+localPort+" is using!Please use another port.");
		}

		//创建Session
		PortForwardingHandler handler = new PortForwardingHandler(remoteSshInfo);
		//开通端口1：使用SSH的远程端口转发，将端口1跟本地localPort关联在一起
		handler.startRemotePortForwarding(remotePort1, "localhost", localPort);
		//开通端口2：在远程主机中开通，全局的SSH本地端口转发（ssh -g -L端口2:localhost:端口1 localhost）
		ShellHandler shellHandler = new ShellHandler(handler.getSession());
		shellHandler.exec(
				String.format("ssh -g -L%1$d:%2$s:%3$d localhost", remotePort2,"localhost", remotePort1),
				remoteSshInfo.getPassword()
		);
		log.info("openRemotePortToLocal result:{}", shellHandler.getResponse());
		log.info("远程端口转发设置成功，请访问{}:{}来测试是否连接成功", remoteSshInfo.getHost(), remotePort2);

		//创建PortLink
		handler.setPortLink(new PortLink(localPort, remoteSshInfo.getHost(), remotePort1));
		portForwardingMap.put(localPort, handler);

		return remotePort2;
	}

	/**
	 * @param localPort
	 */
	public static void stopRemotePortForwarding(int localPort) throws JSchException {
		PortForwardingHandler handler = portForwardingMap.get(localPort);
		if(handler != null) {
			handler.stopRemotePortForwarding(localPort);
		}
	}

	/**
	 * 开通本地到remoteHost的SSH通道
	 * @param sshInfo       中转机子的ssh连接信息
	 * @param localPort     本地端口
	 * @param remoteHost    想要连接的远程机子
	 * @param remotePort    想要连接的远程端口
	 */
	public static int openLocalPortToRemote(
			SshInfo sshInfo,
			int localPort,
			String remoteHost,
			int remotePort) throws JSchException {
		//检查本地端口是否已经占用
		if(localPort>0 && isLocalPortUsing(localPort))
			throw new SSHException("port "+localPort+" is be using!");

		PortForwardingHandler handler = new PortForwardingHandler(sshInfo);
		int result = handler.startLocalPortForwarding(localPort, remoteHost, remotePort);

		log.info("本地端口转发设置成功，请访问{}:{}来测试是否连接成功", "localhost", result);

		//创建PortLink
		handler.setPortLink(new PortLink(localPort, remoteHost, remotePort));
		portForwardingMap.put(result, handler);

		return result;
	}

	/**
	 * 关闭本地的端口映射
	 * @param port
	 * @throws JSchException
	 */
	public synchronized static void stopLocalPortForwarding(int port) throws JSchException {
		PortForwardingHandler handler = portForwardingMap.remove(port);
		if(handler != null) {
			handler.stopLocalPortForwarding(port);
		}
	}

	/**
	 * 检查某个端口是否在使用中
	 * @param port
	 * @return
	 */
	public static boolean isLocalPortUsing(int port) {
		PortForwardingHandler handler = portForwardingMap.get(port);
		if(handler == null)
			return false;
		try {
			return handler.getSession().isConnected();
		} catch (JSchException e) {
			log.error("error on call isLocalPortUsing({})", port);
			return false;
		}
	}

	/**
	 * 停止Session
	 * @param localPort -1时，停止全部的session
	 */
	public static void disConect(int localPort){
		if(localPort == -1){
			portForwardingMap.forEach((k,v)->{
				v.disconnect();
			});
			synchronized (portForwardingMap){
				portForwardingMap.clear();
			}
		}else{
			PortForwardingHandler handler = portForwardingMap.remove(localPort);
			if(handler != null)
				handler.disconnect();
		}
	}

	/**
	 * 上传jar包到远程主机，并运行之
	 * 步骤：
	 * 1.先通过中转主机建立本地跟目标机器的SSH隧道
	 * 2.通过隧道登录到目标主机
	 * 3.判断java环境是否存在，如果不存在，报错
	 * 4.如果步骤3成功了，则上传指定的jar包到目标主机（期间先判断目标主机是否已经存在相同的jar文件，
	 *      如果存在，则根据replaceOnExist来决定是否覆盖）
	 * 5.上成功后，执行startupCmd命令
	 * 6.关闭SSH隧道
	 *
	 * @param remoteInfo
	 * @param jarPath
	 * @param remotePath
	 * @param replaceOnExist
	 * @param startupCmd
	 * @param execTimeout
	 */
	@Deprecated
	public static void uploadAndRunJarOnRemoteHost(
			SshInfo transitInfo,
			SshInfo remoteInfo,
			String jarPath,
			String remotePath,
			boolean replaceOnExist,
			String startupCmd,
			int execTimeout) throws JSchException, IOException, SftpException, InterruptedException {
		PortForwardingHandler portHandler = null;
		SftpHandler sftpHandler = null;
		ShellHandler shellHandler = null;
		try{
			portHandler = new PortForwardingHandler(transitInfo);
			//建立本地端口到目标主机的隧道
			int localPort = portHandler.startLocalPortForwarding(0, remoteInfo.getHost(), remoteInfo.getPort());
			//判断隧道是否搭建成功
			if(localPort <= 0){
				throw new SSHException("无法搭建本地到"+remoteInfo.getHost()+":"+remoteInfo.getPort()+"的SSH隧道");
			}
			log.info("open local port forwarding on {}, to {}:{}", localPort, remoteInfo.getHost(), remoteInfo.getPort());

			//通过隧道登录到目标主机，这时需要一份remoteInfo的备份
			SshInfo newInfo = SerializationUtils.clone(remoteInfo);
			newInfo.setHost("localhost").setPort(localPort);

			shellHandler = new ShellHandler(newInfo);
			//判断是否已经安装了java
			shellHandler.clean().exec("java -version");
			String version=shellHandler.getLastResponse();
			log.info("执行 java -version 命令，返回值：\n{}", version);
			if(version == null || !version.startsWith("java version")){
				throw new SSHException("目标主机没有安装java环境，请先安装最新的java运行环境" +
						"（执行java -version，能看到java version字样就表示安装成功）");
			}

			/*
			上传文件
			 */
			sftpHandler = new SftpHandler(shellHandler.getSession());

			log.info("通过SSH隧道登录到主机{}:{}", remoteInfo.getHost(), remoteInfo.getPort());

			//先判断文件是否存在，并判断是否需要覆盖
			if(replaceOnExist || !sftpHandler.isExist(remotePath)){
				log.info("开始上传本地文件{}到远程{}", jarPath,remotePath);
				//执行文件上传
				sftpHandler.upload(jarPath, remotePath);
				log.info("文件上传完成");
			}

			log.info("执行命令：{}", startupCmd);
			/*
			执行启动命令
			 */
			//切换到当前目录
			int index = remotePath.lastIndexOf("/");
			if(index>=0){
				String cd = "cd "+remotePath.substring(0, index);
				shellHandler.clean().exec(cd);
				log.info("执行命令：{}", cd);
			}
			//Thread.sleep(1000);
			//shellHandler.exec(startupCmd);
			ExecHandler execHandler = new ExecHandler(shellHandler.getSession());
			execHandler.exec(startupCmd, execTimeout);

			if(execHandler.getResultCode()!=0 || StringUtils.isNotBlank(execHandler.getResultError())){
				log.error("执行命令：{} 时出错了\n运行时信息：\n{}错误信息：\n{}",
						startupCmd,
						execHandler.getResult(),
						execHandler.getResultError()
				);

				throw new SSHException("执行'"+startupCmd+"'出错!");
			}
		}
		catch (SSHException e){
			throw e;
		}
		catch (JSchException e){
			throw e;
		}
		catch (Exception e){
			throw e;
		}finally {
			if(shellHandler != null){
				log.info("----------------关闭ShellHandler----------------");
				shellHandler.disconnect();
			}

			if(sftpHandler !=null){
				log.info("----------------关闭SftpHandler----------------");
				sftpHandler.disconnect();
			}
			if(portHandler != null){
				log.info("----------------关闭PortWardingHandler----------------");
				portHandler.disconnect();
			}
		}
	}


	/**
	 * 在指定的远程主机中执行RemoteBootEntity
	 * @param transitInfo
	 * @param ssh
	 * @param rbe
	 * @param execTimeout
	 * @throws JSchException
	 * @throws IOException
	 * @throws SftpException
	 * @throws InterruptedException
	 */
	public static void execRemoteBooeEntity(
			IMessageBuilder mb,
			SshInfo transitInfo,
			SshInfo ssh,
			RemoteBootEntity rbe,
			int execTimeout)
			throws JSchException, IOException, SftpException, InterruptedException{

		mb.put("=============远程操作:开始=============");

		PortForwardingHandler portHandler = null;
		SftpHandler sftpHandler = null;
		ShellHandler shellHandler = null;

		int localPort=0;
		try{
			//通过隧道登录到目标主机，这时需要一份remoteInfo的备份
			SshInfo newInfo = SerializationUtils.clone(ssh);

			//如果使用SSH隧道
			if(transitInfo!=null){
				mb.put("使用SSH安全隧道，代理主机="+transitInfo.getHost());

				portHandler = new PortForwardingHandler(transitInfo);
				//建立本地端口到目标主机的隧道
				localPort = portHandler.startLocalPortForwarding(0, ssh.getHost(), ssh.getPort());
				//判断隧道是否搭建成功
				if(localPort <= 0){
					throw new SSHException("无法搭建本地到"+ssh.getHost()+":"+ssh.getPort()+"的SSH隧道");
				}
				log.info("open local port forwarding on {}, to {}:{}", localPort, ssh.getHost(), ssh.getPort());
				mb.put("开启本地端口%1$d到远程主机%2$s:%3$d的SSH隧道", localPort, ssh.getHost(), ssh.getPort());

				newInfo.setHost("localhost").setPort(localPort);
			}else{
				mb.put("不使用SSH安全隧道【不推荐】，由本机直接链接到目标主机");
			}

			shellHandler = new ShellHandler(newInfo);

			//是否检查java环境
			if(rbe.isCheckJava()){
				mb.put("检查 java 运行环境（执行 java -version）");
				//判断是否已经安装了java，如果没有java环境，则不能继续操作
				shellHandler.clean().exec("java -version");
				String version=shellHandler.getLastResponse();
				log.info("执行 java -version 命令，返回值：\n{}", version);
				mb.put("[返回内容]\n"+version);

				if(version == null || !version.startsWith("java version")){
					mb.put("目标主机没有安装java环境，请先安装最新的java运行环境" +
							"（执行java -version，能看到java version字样就表示安装成功）");
					throw new SSHException(mb.getLast());
				}
			}

			//执行beforeCommand
			for(String cmd:rbe.getBeforeCommands()){
				mb.put("执行命令："+cmd);
				shellHandler.clean().exec(cmd);
				mb.put("[返回内容]\n"+shellHandler.getLastResponse());
				log.info("执行{}命令，返回值：\n{}", cmd, shellHandler.getLastResponse());
			}

			sftpHandler = new SftpHandler(shellHandler.getSession());
			if(rbe.isClean()){
				try{
					sftpHandler.rmDir(rbe.getRemotePath());
				}catch (Exception e){
					mb.put("清空远程目录时出错：" + e.getMessage());
					log.error(mb.getLast());

					throw e;
				}
			}
			/*
			上传文件
			 */
			if(rbe.getFileList()!=null && rbe.getFileList().size()>0) {
				log.info("通过SSH隧道登录到主机{}:{}", ssh.getHost(), ssh.getPort());

				//判断远程目录是否存在
				if (!sftpHandler.isExist(rbe.getRemotePath())) {
					mb.put("远程目录%1$s不存在，现在尝试创建...", rbe.getRemotePath());
					log.info(mb.getLast());

					try {
						sftpHandler.mkdir(rbe.getRemotePath());

						mb.put("目录创建成功");
					} catch (Exception e) {
						mb.put("创建目录时出错：" + e.getMessage());
						log.error(mb.getLast());

						throw e;
					}
				}

				//上传文件
				for (File file : rbe.getFileList()) {
					String name = file.getName();
					String remoteP = rbe.getRemotePath(name);
					//先判断文件是否存在，并判断是否需要覆盖
					if (rbe.isReplace() || !sftpHandler.isExist(remoteP)) {
						mb.put("开始上传本地文件%1$s到远程%2$s", file.getAbsoluteFile(), remoteP);
						log.info(mb.getLast());
						//执行文件上传
						sftpHandler.upload(file, remoteP);

						mb.put("文件上传完成");
						log.info(mb.getLast());
					}
				}
			}

			//切换到当前目录
			String cd = "cd "+rbe.getRemotePath();
			shellHandler.clean().exec(cd);

			mb.put("执行命令：%1$s", cd);
			log.info(mb.getLast());

			mb.put("-----------开始执行afterCommands-------------");
			for(String cmd:rbe.getAfterCommands()){
				mb.put("执行命令："+cmd);
				shellHandler.clean().exec(cmd);
				mb.put("[返回内容]\n"+shellHandler.getResponse());
			}
			/*
			执行startup命令
			 */
//			for(String cmd:rbe.getAfterCommands()){
//
//			}

			mb.put("-----------开始执行启动命令:"+rbe.getStartupCommand());
			log.info(mb.getLast());
			if(StringUtils.isNotBlank(rbe.getStartupCommand())){
				ExecHandler execHandler = new ExecHandler(shellHandler.getSession());
				execHandler.exec(rbe.getStartupCommand(), execTimeout);

				mb.put("执行启动命令：%1$s", rbe.getStartupCommand());

				if(execHandler.getResultCode()!=0 || StringUtils.isNotBlank(execHandler.getResultError())){
					mb.put("执行启动命令：%1$s 时出错了\n运行时信息：\n%2$s错误信息：\n%3$s",
							rbe.getStartupCommand(),
							execHandler.getResult(),
							execHandler.getResultError());
					log.error(mb.getLast());

					throw new SSHException("执行启动命令'"+rbe.getStartupCommand()+"'出错!");
				}else{
					mb.put("\n%1$s", execHandler.getResult());
				}
			}
		}
		catch (SSHException e){
			throw e;
		}
		catch (JSchException e){
			throw e;
		}
		catch (Exception e){
			throw e;
		}finally {
			if(localPort>0)
				portHandler.stopLocalPortForwarding(localPort);

			if(shellHandler != null){
				log.info("----------------关闭ShellHandler----------------");
				shellHandler.disconnect();
			}

			if(sftpHandler !=null){
				log.info("----------------关闭SftpHandler----------------");
				sftpHandler.disconnect();
			}
			if(portHandler != null){
				log.info("----------------关闭PortWardingHandler----------------");
				portHandler.disconnect();
			}
			mb.put("=============远程操作:结束=============");
		}
	}

	/**
	 * 获取本地与外部主机之间的端口关联
	 * @return
	 */
	public static List<PortLink> getPortLinks(){
		List<PortLink> links = new ArrayList<>();
		portForwardingMap.forEach((k,v)->links.add(SerializationUtils.clone(v.getPortLink())));
		return links;
	}

	/**
	 * 对SSH进行连接测试
	 * @param transitInfo       中转机子，如果为空，则通过本地机子直接链接远程主机
	 * @param remoteInfo
	 * @return
	 * @throws JSchException
	 */
	public static synchronized boolean testForSSH(
			SshInfo transitInfo,
			SshInfo remoteInfo) throws JSchException {
		PortForwardingHandler portHandler=null;
		ShellHandler shellHandler = null;
		SshInfo newInfo = SerializationUtils.clone(remoteInfo);

		int localPort = 0;
		log.info("SSH 到 {}:{}", remoteInfo.getHost(), remoteInfo.getPort());
		try{
			//代理机器不为空时，建立SSH通讯隧道
			if(transitInfo!=null){
				portHandler = new PortForwardingHandler(transitInfo);
				//建立本地端口到目标主机的隧道
				localPort = portHandler.startLocalPortForwarding(0, remoteInfo.getHost(), remoteInfo.getPort());
				//判断隧道是否搭建成功
				if(localPort <= 0){
					throw new SSHException("无法搭建本地到"+remoteInfo.getHost()+":"+remoteInfo.getPort()+"的SSH隧道");
				}
				log.info("open local port forwarding on {}, to {}:{}", localPort, remoteInfo.getHost(), remoteInfo.getPort());

				//通过隧道登录到目标主机，这时需要一份remoteInfo的备份
				newInfo.setHost("localhost").setPort(localPort);
			}

			log.info("start to open Session....");
			//建立连接
			shellHandler = new ShellHandler(newInfo);
			//执行命令
			shellHandler.clean().exec("echo Hello world!");
			System.out.println(shellHandler.getLastResponse());

			return shellHandler.isConnected();
		}
		catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("无法建立SSH连接，请检查连接参数.错误信息："+e.getMessage());
		}finally {
			//如果开启了SSH隧道，需要关闭
			if(localPort>0){
				portHandler.stopLocalPortForwarding(localPort);
			}

			if(shellHandler != null){
				log.info("----------------关闭ShellHandler----------------");
				shellHandler.disconnect();
			}
			if(portHandler != null){
				log.info("----------------关闭PortWardingHandler----------------");
				portHandler.disconnect();
			}
		}
	}
}