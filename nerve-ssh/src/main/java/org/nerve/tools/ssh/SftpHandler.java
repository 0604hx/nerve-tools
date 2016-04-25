package org.nerve.tools.ssh;

import com.jcraft.jsch.*;
import com.zeus.ssh.bean.SshInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 基于SFTP的文件上传、下载
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public class SftpHandler extends AbstractSessionHandler {
	public SftpHandler(SshInfo sshInfo){
		super(sshInfo);
	}
	public SftpHandler(Session session){
		super(session);
	}

	private ChannelSftp channel;

	public void upload(String filePath, String remotePath) throws JSchException, IOException, SftpException {
		upload(new File(filePath), remotePath);
	}

	public void upload(File file, String remotePath) throws JSchException, IOException, SftpException {
		OutputStream outputStream = getChannel().put(remotePath);
		FileUtils.copyFile(file, outputStream);
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * 将指定内容写入远程文件中
	 * @param content
	 * @param remotePath
	 */
	public void writeContentTo(CharSequence content, String remotePath) throws JSchException, SftpException, IOException {
		OutputStream outputStream = getChannel().put(remotePath);
		IOUtils.write(content, outputStream);
	}

	/**
	 * 下载远程文件到本地
	 * @param remotePath
	 * @param localPath
	 * @throws JSchException
	 * @throws IOException
	 * @throws SftpException
	 */
	public void download(String remotePath, String localPath) throws JSchException, IOException, SftpException{
		download(remotePath, new File(localPath));
	}

	public void download(String remotePath, File file) throws JSchException, SftpException, IOException {
		FileUtils.copyInputStreamToFile(getChannel().get(remotePath), file);
	}

	/**
	 * 判断远程主机指定路径是否存在（文件或者目录）
	 * @param path
	 * @return
	 * @throws JSchException
	 * @throws SftpException
	 */
	public boolean isExist(String path) throws JSchException, SftpException {
		try{
			SftpATTRS attrs = getChannel().stat(path);
			return attrs!=null;
		}catch (Exception e){
			log.error("error on check file exist:{}:{}", path, e.getMessage());
			return false;
		}
	}

	/**
	 * 创建目录，同时赋予775权限
	 * @param path
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void mkdir(String path) throws JSchException, SftpException {
		getChannel().mkdir(path);
		//getChannel().chmod(777,path);
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if(channel != null)
			channel.disconnect();
	}

	private ChannelSftp getChannel() throws JSchException {
		if(channel==null || channel.isClosed() || !channel.isConnected()){
			channel =(ChannelSftp) getSession().openChannel("sftp");
			channel.connect();
		}

		return channel;
	}
}
