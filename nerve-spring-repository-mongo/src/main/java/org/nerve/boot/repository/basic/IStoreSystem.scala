package org.nerve.boot.repository.basic

import java.io.InputStream

/**
  * 资源存储接口，提供存储、提取、删除三个方法
  * Created by zengxm on 2016/2/25 0025.
  */
trait IStoreSystem {

  /**
    * 储存
    * @param is   输入流
    * @param fileName 保存的路径名
    * @return 返回存储唯一ID
    */
  def store(is:InputStream, fileName:String):String

  /**
    * 根据唯一ID删除文件
    * @param id
    * @return
    */
  def delete(id:String):Boolean

  /**
    * 根据唯一ID获取文件
    * @param id
    * @return
    */
  def find(id:String):InputStream
}
