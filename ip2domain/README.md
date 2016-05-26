# 概述
本组件的目的是根据IP（尽可能）得到相关的域名。

更多介绍请看 _documents/README.md

# 如何使用？
```java
Ip2DomainByGlobalBingSearch api=new Ip2DomainByGlobalBingSearch();

System.out.println(api.lookup("125.39.240.113"));
System.out.println(api.lookup("14.215.177.37"));
System.out.println(api.lookup("180.97.164.26"));
System.out.println(api.lookup("101.201.172.229"));

```
