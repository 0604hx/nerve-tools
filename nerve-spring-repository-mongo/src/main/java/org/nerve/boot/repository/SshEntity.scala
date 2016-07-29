package org.nerve.boot.repository

import java.util.Date

import org.nerve.boot.repository.bean.{SshBean, Status}

import scala.beans.{BeanProperty, BeanDescription}

/**
  * Created by zengxm on 2016/3/1 0001.
  */
class SshEntity extends BaseEntity{
  @BeanDescription("SSH链接信息")
  @BeanProperty var ssh:SshBean=_
  @BeanDescription("RPC调用的端口，默认是9090")
  @BeanProperty var rpcPort=9090

  @BeanProperty var status= Status.PENDING
  @BeanDescription("最后通信时间")
  @BeanProperty var lastTouchTime:Date=_
  @BeanProperty var lastStatus:String=_ //最后操作状态

  @BeanProperty var retryTimes:Int=0  //连接尝试次数

  /**
    * 连接失败
    */
  def connectFail=retryTimes+=1

  /**
    * 连接成功时
    */
  def connectSuccess=retryTimes=0
}
