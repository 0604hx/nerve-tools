# 概述
分布式文件系统辅助工具。支持功能：

1. 上传文件、文件夹
2. 导入文件
3. 文件搜索（基于文件名，日期等）
4. 文件删除

# 如何引用
此项目使用到了javaxt-core，请先下载javaxt-core-{versionCode}.jar

然后安装到本地maven：
```java
>mvn install:install-file -Dfile=D:\workspace\intellij15\nerve-tools\nerve-fs\libs\javaxt-core-1.7.5.jar -DgroupId=javaxt -DartifactId=javaxt-core -Dversion=1.7.5 -e -Dpackaging=jar
```

即可。

# LOGS
## 1.0.5
1. DFileSystem接口增加setCategory方法，可以设置文件的大分类

