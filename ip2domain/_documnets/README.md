# 概述
本组件的目的是根据IP（尽可能）得到相关的域名

# 调研
## maxmind IP/域名 数据库

[maxmind GeoIP2 domain name Database](https://www.maxmind.com/zh/geoip2-domain-name-database) 可以查询与IPv4、IPv6地址相关的二级域名。
 数据库将会包括"example.com"或"example.co.uk"，但不会包括整个域名，例如"foo.example.com"。
  数据库从IP地址所属的/24网段返回一个二级域名样本。 因此，只有在整个/24网段均使用同一个二级域名样本的情况下， 它才会返回正确的数据。 
  数据库包含大约12.8万个二级域名。

## ISC域名数据集
[ISC domain DataSet](https://www.isc.org/product/domain-survey-quarterly-dataset/) 

## Bing search api
[Bing search api](https://datamarket.azure.com/dataset/bing/search) 也可以查询到domain，但是需要进行后续的数据筛选。

使用前需要注册，[请看说明](http://www.secbox.cn/hacker/440.html)

注意:
+ 使用bing search api 需要VPN的支持（大陆环境下，同样的api调用，无法得到准确的数据结果）

## Global Bing Search
无论在哪个地方，只要设置了正确的cookie，就能使用global.bing.com 进行ip查询。

