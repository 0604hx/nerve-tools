package org.nerve.tools.ssh;

import org.junit.Test;

/**
 * com.zues.ssh.test
 * Created by zengxm on 2015/12/11 0011.
 */
public class ShellTest extends AbstractSshTest{

	@Test
	public void shell(){
		ShellHandler handler = new ShellHandler(getSshInfo());
		handler.exec("java -version");

		System.out.println(handler.getResponse());
	}
}