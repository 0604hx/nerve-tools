package org.nerve.tools.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.nerve.tools.ssh.bean.SshInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * com.zeus.ssh
 * Created by zengxm on 2015/12/10 0010.
 */
public class ExecHandler extends AbstractSessionHandler {

	private ChannelExec channel;
	private String result;
	private int resultCode;
	private String resultError;

	private static final long timeStep = 100;

	public ExecHandler(SshInfo sshInfo){
		super(sshInfo);
	}
	public ExecHandler(Session session){
		super(session);
	}

	public String getResult() {
		return result;
	}

	public int getResultCode() {
		return resultCode;
	}

	public String getResultError() {
		return resultError;
	}

	private void beforeExec(){
		result = null;
		resultError = null;
		resultCode = -1;
	}

	public int exec(String cmd, int timeout) throws JSchException, IOException, InterruptedException {
		beforeExec();

		ChannelExec channelExec = (ChannelExec) getSession().openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		channelExec.setErrStream(baos);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();
		StringBuffer buf = new StringBuffer(1024);

		long useTime = 0;

		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0) break;
				String line = new String(tmp, 0, i);
				System.out.println("||| "+line);
				buf.append(line);
			}
			if (channelExec.isClosed()) {
				resultCode = channelExec.getExitStatus();
				break;
			}
			Thread.sleep(timeStep);
			useTime+=timeStep;

			if(timeout>0 && useTime>=timeout*1000){
				System.out.println(String.format("目前已经监听运行程序%1$d秒，超出了预设的%2$d秒，现在停止监听（但是远程程序不受影响）",useTime/1000, timeout));
				log.info(
						"目前已经监听运行程序{}秒，超出了预设的{}秒，现在停止监听（但是远程程序不受影响）",
						useTime/1000,
						timeout
				);
				resultCode = 0;
				break;
			}
		}
//
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//		String line;
//		int index = 0;
//		while ((line = reader.readLine()) != null) {
//			System.out.println("| "+index + " | " + line);
//			buf.append(line);
//		}
//		resultCode = channelExec.getExitStatus();

		result = buf.toString();
		resultError = baos.toString();
		channelExec.disconnect();
		return resultCode;
	}
}
