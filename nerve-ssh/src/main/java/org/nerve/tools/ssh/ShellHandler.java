package org.nerve.tools.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;
import org.nerve.tools.ssh.bean.SshInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public class ShellHandler extends AbstractSessionHandler {

	private ChannelShell channel;
	private Expect4j expect = null;
	private static final long defaultTimeOut = 1000;
	private StringBuffer buffer=new StringBuffer();

	public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
	public static final String BACKSLASH_R = "\r";
	public static final String BACKSLASH_N = "\n";
	public static final String COLON_CHAR = ":";
	public static String ENTER_CHARACTER = BACKSLASH_R;

	//正则匹配，用于处理服务器返回的结果
	public static String[] linuxPromptRegEx = new String[] { "~]#", "~#", "#",
			":~#", "/$", ">" };

	public static String[] errorMsg=new String[]{"could not acquire the config lock "};

	private boolean responeWithoutCommand = true;        //responde 中是否包含命令本身
	private String currentCommand;  //当前执行的命令
	private String lastResponse;    //最后的一条命令的回复信息

	private List<Match> patternList;

	public ShellHandler(SshInfo sshInfo){
		super(sshInfo);
		expect = getExpect();
	}

	public ShellHandler(Session session){
		super(session);
		expect = getExpect();
	}

	public boolean isResponeWithoutCommand() {
		return responeWithoutCommand;
	}

	public ShellHandler setResponeWithoutCommand(boolean responeWithoutCommand) {
		this.responeWithoutCommand = responeWithoutCommand;
		return this;
	}

	public String getCurrentCommand() {
		return currentCommand;
	}

	public String getLastResponse() {
		return lastResponse;
	}

	public String getResponse(){
		return buffer.toString();
	}

	public ShellHandler clean(){
		buffer.setLength(0);
		return this;
	}

	//获得Expect4j对象，该对用可以往SSH发送命令请求
	private Expect4j getExpect() {
		try {
			channel = (ChannelShell) getSession().openChannel("shell");
			channel.setPty(true);
			channel.setEnv("nerve", "hello, I am from nerve");
			Expect4j expect = new Expect4j(channel.getInputStream(), channel
					.getOutputStream());
			channel.connect();

			if(sshInfo != null)
				log.info("open session shell successful on {}@{}:{}", sshInfo.getUser(), sshInfo.getHost(), sshInfo.getPort());

			initExpect();
			return expect;
		} catch (Exception ex) {
			log.error("failed open session shell", ex);
			//ex.printStackTrace();
			throw new RuntimeException("failed open session shell："+ex.getMessage());
		}
	}

	private void initExpect() throws Exception{
		Closure closure = new Closure(){
			public void run(ExpectState expectState) throws Exception {
				String re = expectState.getBuffer().trim();
				//获取最近执行命令的回复信息
				if(currentCommand != null && re.startsWith(currentCommand)){
					String re2 = re.replace(currentCommand, "");
					if(responeWithoutCommand){
						int index = re2.indexOf("\n");
						if(index <=1)
							re2 = re2.substring(index+1);
					}
					lastResponse = re2;
				}
				buffer.append(re);// buffer is string
				// buffer for appending
				// output of executed
				// command
				expectState.exp_continue();
			}
		};
		patternList = new ArrayList<Match>();
		String[] regEx = linuxPromptRegEx;
		if (regEx != null && regEx.length > 0) {
			synchronized (regEx) {
				for (String regexElement : regEx) {// list of regx like, :>, />
					// etc. it is possible
					// command prompts of your
					// remote machine
					try {
						RegExpMatch mat = new RegExpMatch(regexElement, closure);
						patternList.add(mat);
					} catch (Exception e) {
						throw e;
					}
				}
				patternList.add(new EofMatch(new Closure() { // should cause
					// entire page to be
					// collected
					public void run(ExpectState state) {
					}
				}));
				patternList.add(new TimeoutMatch(defaultTimeOut, new Closure() {
					public void run(ExpectState state) {
					}
				}));
			}
		}
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if(expect != null)
			expect.close();
		if(channel != null)
			channel.disconnect();
	}

	/**
	 * 执行命令，只有全部都执行成功后，才返回true
	 * @param commands      待执行的命令
	 * @return              true if all commands is done
	 */
	public boolean exec(String... commands){
		//如果expect返回为0，说明登入没有成功
		if(expect==null){
			return false;
		}

		log.debug("----------Running commands are listed as follows:----------");
		for(String command:commands){
			log.debug(command);
		}
		log.debug("----------End----------");

		try {
			boolean isSuccess = true;
			for (String cmd : commands){
				isSuccess = isSuccess(patternList, cmd);
			}
			//防止最后一个命令执行不了
			isSuccess = !checkResult(expect.expect(patternList));
			//找不到错误信息标示成功
			String response=buffer.toString().toLowerCase();
			for(String msg:errorMsg){
				if(response.indexOf(msg)>-1){
					return false;
				}
			}
			return isSuccess;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	//检查执行是否成功
	private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
		try {
			boolean isFailed = checkResult(expect.expect(objPattern));
			if (!isFailed) {
				currentCommand = strCommandPattern;
				expect.send(strCommandPattern+"\r");
				return true;
			}
		} catch (Exception ex) {
			log.error("error on call isSuccess()", ex);
		}
		return false;
	}

	//检查执行返回的状态
	private boolean checkResult(int intRetVal) {
		if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
			return true;
		}
		return false;
	}
}
