package org.nerve.tools.ssh.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 远程启动Bean，用于SSH登录到远程主机，上传文件并执行指定命令
 * 此对象包含以下内容：
 * 1.上传前执行命令  beforeCommand
 * 2.需要上传的文件列表  fileList
 * 3.远程目录地址        remotePath
 * 4.是否覆盖            replace
 * 5.上传完成后执行的命令 afterCommand
 *
 * -----------------------------------------------------------------
 * modify logs
 * -------------------------------
 * 2016年4月26日10:58:38   增加clean属性，为true时清空远程目录
 *
 *
 * com.zeus.ssh.bean
 * Created by zengxm on 2016/2/23 0023.
 */
public class RemoteBootEntity implements Serializable{
	private List<String> beforeCommands;
	private List<String> afterCommands;
	private String startupCommand;  //启动命令，一般只写一条
	private List<File> fileList;
	private boolean replace;
	private String remotePath;
	private boolean checkJava;      //是否检查java环境
	private boolean clean;          //是否清空远程目录

	public boolean isClean() {
		return clean;
	}

	public RemoteBootEntity setClean(boolean clean) {
		this.clean = clean;
		return this;
	}

	public String getStartupCommand() {
		return startupCommand;
	}

	public RemoteBootEntity setStartupCommand(String startupCommand) {
		this.startupCommand = startupCommand;
		return this;
	}

	/**
	 * 增加一条上传前的执行命令
	 * @param cmds
	 * @return
	 */
	public RemoteBootEntity addBeforeCommand(String... cmds){
		if(beforeCommands==null)
			beforeCommands=new ArrayList<>();
		for(String c:cmds)
			beforeCommands.add(c);
		return this;
	}
	public RemoteBootEntity addAfterCommand(String... cmds){
		if(afterCommands==null)
			afterCommands=new ArrayList<>();
		for(String c:cmds)
			afterCommands.add(c);
		return this;
	}

	public RemoteBootEntity addFile(String filePath) throws FileNotFoundException {
		return addFile(new File(filePath));
	}

	public RemoteBootEntity addFile(File file) throws FileNotFoundException {
		if(file == null || !file.exists())
			throw new FileNotFoundException("can not upload file not exist!,path="+file.getAbsolutePath());

		if(fileList==null)
			fileList=new ArrayList<>();
		fileList.add(file);
		return this;
	}

	public List<String> getBeforeCommands() {
		if(beforeCommands==null)
			beforeCommands=new ArrayList<>();
		return beforeCommands;
	}

	public RemoteBootEntity setBeforeCommands(List<String> beforeCommands) {
		this.beforeCommands = beforeCommands;
		return this;
	}

	public List<String> getAfterCommands() {
		if(afterCommands==null)
			afterCommands=new ArrayList<>();
		return afterCommands;
	}

	public RemoteBootEntity setAfterCommands(List<String> afterCommands) {
		this.afterCommands = afterCommands;
		return this;
	}

	public List<File> getFileList() {
		return fileList;
	}

	public RemoteBootEntity setFileList(List<File> fileList) {
		this.fileList = fileList;
		return this;
	}

	public boolean isReplace() {
		return replace;
	}

	public RemoteBootEntity setReplace(boolean replace) {
		this.replace = replace;
		return this;
	}

	public String getRemotePath() {
		return remotePath;
	}

	/**
	 * 使用Linux文件分隔符
	 * @param name
	 * @return
	 */
	public String getRemotePath(String name){
		return remotePath+"/"+name;
	}

	public RemoteBootEntity setRemotePath(String remotePath) {
		this.remotePath = remotePath;
		return this;
	}

	public boolean isCheckJava() {
		return checkJava;
	}

	public RemoteBootEntity setCheckJava(boolean checkJava) {
		this.checkJava = checkJava;
		return this;
	}
}
