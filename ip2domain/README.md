# 概述
本组件的目的是根据IP（尽可能）得到相关的域名。

更多介绍请看 _documents/README.md

# 如何使用？
``` java
Ip2DomainByGlobalBingSearch api=new Ip2DomainByGlobalBingSearch();

System.out.println(api.lookup("125.39.240.113"));
System.out.println(api.lookup("14.215.177.37"));
System.out.println(api.lookup("180.97.164.26"));
System.out.println(api.lookup("101.201.172.229"));
```

# 扩展	extends

如果你想定义自己的解析方法，可以实现Ip2Domain接口，或者继承于AbstractHttpIp2Domain。详细的请看源码。

# 要求 Required
JDK 8+ 		for lamda


# LOG
## v 1.4
1. 取消配置文件。如果需要增加domain抽取的正则表达式，请调用 Ip2DomainFromUrl.addUrlRegs 方法

## v 1.4.1
1. 修复无法通过global获取到域名信息的BUG
