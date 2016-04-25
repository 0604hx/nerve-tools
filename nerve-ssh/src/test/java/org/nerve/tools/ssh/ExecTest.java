package org.nerve.tools.ssh;

import com.jcraft.jsch.JSchException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * com.zues.ssh
 * Created by zengxm on 2015/12/11 0011.
 */
public class ExecTest extends AbstractSshTest {

	protected ExecHandler handler ;

	@Before
	public void init(){
		handler = new ExecHandler(getSshInfo());
	}

	@After
	public void after(){
		if(handler != null)
			handler.disconnect();
	}

	@Test
	public void execJar() throws InterruptedException, JSchException, IOException {
		handler.exec("java -jar /root/thrift-server.jar 192.168.1.131", 10);
		log.info("exec执行完成，结果为：\nresult:\n{} \nerror:\n{}",
				handler.getResult(),
				handler.getResultError());
	}
}
