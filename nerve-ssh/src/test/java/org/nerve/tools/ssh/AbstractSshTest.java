package org.nerve.tools.ssh;

import org.nerve.tools.ssh.bean.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * com.zues.ssh
 * Created by zengxm on 2015/12/11 0011.
 */
public class AbstractSshTest {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	public SshInfo getSshInfo(){
		return new SshInfo( "192.168.1.131","root", "jiepu2015", 22);
	}
}
