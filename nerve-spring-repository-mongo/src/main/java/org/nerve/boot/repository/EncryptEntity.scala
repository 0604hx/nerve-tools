package org.nerve.boot.repository

import scala.beans.{BeanDescription, BeanProperty}

/**
  * 具备RSA属性的实体对象
  * Created by zengxm on 2016/4/25.
  */
class EncryptEntity extends SshEntity{
  @BeanDescription("用于与此终端交互的AES密钥")
  @BeanProperty var aes:String=_
  @BeanDescription("RSA私钥，用于解密调度器数据")
  @BeanProperty var rsaPrivateKey:String=_
  @BeanDescription("RSA公钥，用于加密传输给采集器的数据实体")
  @BeanProperty var rsaPublicKey:String=_
  /**是否使用代理进行SSH链接，默认是开启的*/
  @BeanProperty var useTransit:Boolean=true
}
