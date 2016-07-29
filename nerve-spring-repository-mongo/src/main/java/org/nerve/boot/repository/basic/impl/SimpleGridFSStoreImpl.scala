package org.nerve.boot.repository.basic.impl

import java.io.InputStream

import org.nerve.boot.repository.basic.IStoreSystem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service

/**
  * 对IStoreSystem的mongodb封装
  * Created by zengxm on 2016/2/25 0025.
  */
@Service
class SimpleGridFSStoreImpl extends IStoreSystem{

  @Autowired
  var gridFsTemplate:GridFsTemplate=_

  /**
    * 储存
    * @param is   输入流
    * @param fileName 保存的路径名
    * @return 返回存储唯一ID
    */
  override def store(is: InputStream, fileName: String): String = {
    val file=gridFsTemplate.store(is, fileName)
    file.getId.toString
  }

  /**
    * 根据唯一ID删除文件
    * @param id
    * @return
    */
  override def delete(id: String): Boolean = {
    gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)))
    true
  }

  /**
    * 根据唯一ID获取文件
    * @param id   UUID
    * @return     返回数据流
    */
  override def find(id: String): InputStream = {
    val file=gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)))
    if(file !=null)
      return file.getInputStream

    null
  }
}
