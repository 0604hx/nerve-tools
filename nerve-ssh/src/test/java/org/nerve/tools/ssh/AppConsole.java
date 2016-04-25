package org.nerve.tools.ssh;

import com.zeus.ssh.bean.SshInfo;

import java.io.File;
import java.util.Scanner;

/**
 * 完成功能：
 * 1.设置中转服务器的端口转发（所有中转器端口的请求都转发到本地端口）
 * 2.通过ssh登录到远程服务器，检查是否已经有了java环境，如果没有则不能继续执行；如果有了就上传jar包
 *      （本程序不提供java的安装功能，因为上传一个java安装包太费时间）
 * 3.启动远程服务器的jar包
 * 4.设置本地到远程服务器（运行jar的机子）之间的SSH隧道
 * 5.测试端口是否都正常
 *
 * com.jiepu.ssh.test
 * Created by zengxm on 2015/12/10 0010.
 */
public class AppConsole {

	private static SshInfo transitInfo;    //中转机子的配置信息
	private static String jarPath;
	private static String remotePath;
	private static boolean replace = false;

	public static void init(){
		transitInfo = new SshInfo(
				"192.168.1.176",
				"root",
				"toor",
				22
		);

		String classpath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
		jarPath = classpath+"thrift-server.jar";
		remotePath = "/home/#user#";
	}

	public static void printHelp(){
		System.out.println("-----------------------------------------");
		System.out.println("欢迎使用SSH演示程序");
		System.out.println("-----------------------------------------");
		System.out.println("below commands are available:");
		System.out.println("help    show help message");
		System.out.println("L       添加本地到远程主机的SSH隧道，参数格式为：localPort:remoteHost:remotePort， " +
				"如想要将本地的9000端口映射到192.168.1.131的10080端口，则输入");
		System.out.println("        L 9000:192.168.1.131:10080");
		System.out.println("R       添加远程端口到本地的映射，参数格式为：remotePort:localhost:localPort，" +
				"如将中转机子的9002端口映射到本地的80端口（即所有访问中转机子9002端口的请求都转发到本地80端口），则输入");
		System.out.println("        R 9002:localhost:80");
		System.out.println("A       对某个主机进行Jar程序初始化（上传jar包到目标机子，然后运行），参数格式为：user|password|host|port，" +
				"如");
		System.out.println("        A root|toor|192.168.1.176:22");
		System.out.println("        A root|jiepu2015|192.168.1.131|22");
		System.out.println("PL      设置需要上传的本地jar文件路径（注意，必须是绝对路径,默认是：classpath:java/thrift-server.jar），如：");
		System.out.println("        PL D:/server.jar");
		System.out.println("PR      设置远程目录（上传文件），注意必须是全路径(后尾不加/，#user#会被替换成用户名)，默认是放置到登录用户目录:"+remotePath+"，e.g:");
		System.out.println("        PR /home/nerve/");
		System.out.println("RE      设置是否在上传时覆盖目标主机的文件，默认false。 接受参数：true，false");
		System.out.println("exit    exit the program");
		System.out.println("-----------------------------------------");
	}

	public static void main(String[] args) {
		init();
		//开始接受控制台输入
		Scanner scanner = new Scanner(System.in);
		String line = null;

		printHelp();
		System.out.println("please input something");

		while(!"exit".equalsIgnoreCase(line = scanner.nextLine())){
			//分解line
			String[] ks = line.split(" ");
			try{
				switch (ks[0].toLowerCase()){
					case "help": printHelp(); break;
					case "r":
						//分隔参数
						if(ks.length<2){
							error("参数错误，请参考help");
							break;
						}
						String[] ps = ks[1].split(":");
						SSHFactory.openRemotePortToLocal(transitInfo, Integer.parseInt(ps[0]), Integer.parseInt(ps[2]));
						break;
					case "l":
						//分隔参数
						if(ks.length<2){
							error("参数错误，请参考help");
							break;
						}
						ps = ks[1].split(":");
						SSHFactory.openLocalPortToRemote(transitInfo, Integer.parseInt(ps[0]), ps[1], Integer.parseInt(ps[2]));
						break;
					//自动部署jar到远程主机
					case "a":
						if(ks.length<2){
							error("参数错误，请参考help");
							break;
						}
						ps = ks[1].split("\\|");
						//构建Sshinfo
						SshInfo targetInfo = new SshInfo(
								ps[2],
								ps[0],
								ps[1],
								Integer.valueOf(ps[3])
						);

						//构造remotePath
						String rPath = remotePath.replaceAll("#user#", targetInfo.getUser());
						if(targetInfo.getUser().equals("root")){
							rPath = "/root";
						}
						//获取文件名
						File file = new File(jarPath);

						SSHFactory.uploadAndRunJarOnRemoteHost(
								transitInfo,
								targetInfo,
								jarPath,
								rPath+"/"+file.getName(),
								replace,
								"java -jar "+file.getName()+" "+ps[2],
								10
						);
						break;
					case "pl":
						jarPath = ks[1];
						System.out.println("本地jar路径更新为："+jarPath);
						break;
					case "pr":
						remotePath = ks[1];
						System.out.println("远程路径更新为："+remotePath);
						break;
					case "re":
						replace = Boolean.valueOf(ks[1]);
						System.out.println("是否覆盖目标文件更新为："+replace);
						break;
					default:
						System.out.println("please input correct command!");
						break;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		System.out.println("The Client will be exit now!");
		System.exit(0);
	}

	public static void error(String message){
		System.err.println(message);
	}
}
