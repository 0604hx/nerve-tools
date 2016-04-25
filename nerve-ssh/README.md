# How to use

## 1.本地端口监听（Local PortForwarding)
通常LocalPortForward使用场景为，想要连接到目标主机（ip：8.8.8.8，端口：22），但是不想本机直连而是通过中转机。
此时可以先建立本地与中转机的SSH隧道（如将本地10080端口与8.8.8.8:22联通），此时访问localhost:10080 即可以访问到
目标主机。访问结束后，关闭本机与中转机的隧道。

### 方式一
``` java
//transit host
SshInfo sshInfo=new SshInfo();
sshInfo.setHost("192.168.1.1")
        .setPort(22)
        .setPassword("password")
        .setUser("root");

PortForwardingHandler handler=new PortForwardingHandler(sshInfo);
int localPort=handler.startLocalPortForwarding(10080, "8.8.8.8",22);
//do something
//....

//stop local port listening
handler.stopLocalPortForwarding(localPort);
handler.disconnect();
```

### 方式二
``` java
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
```
## 远程端口转发（Remote PortForwarding）
应用场景：
我在本地部署了对外提供服务的主机（端口9090），但是不想让客户端（使用我服务的人）直接访问我的主机，这时可以使用一台中转
主机，将中转机的8080端口映射到本地，然后客户端访问中转机的8080端口的数据都转发到本地的9090端口。

### Demo
``` java
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
```

# Q&A
## 执行java -jar 命令时出现 java: command not found 怎么办？
答： 这时因为环境没有配置正确，编辑 /etc/environment, 在PATH中加入java所在路径即可（如 /usr/java/bin)，然后刷新配置：source /etc/environment
