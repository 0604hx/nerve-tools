package org.nerve.boot.repository.basic

import java.util

import org.nerve.boot.repository.IdEntity
import org.nerve.boot.repository.tools.Pagination
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.data.repository.CrudRepository

/**
  * Created by zengxm on 2016/4/25.
  */
trait CommonService[T<:IdEntity,R<:CrudRepository[T,String]] {

  /**
    * 获取指定id的实体对象
    * @param id
    * @return
    */
  def get(id:String):T

  /**
    * 保存实体
    * @param t
    */
  def save(t:T)

  /**
    * 保存前进行的操作
    * @param t
    * @return 返回false时，保存操作将终止
    */
  def onBeforeSave(t:T):Boolean


  def delete(t:T):Unit

  def delete(id:String):Unit

  /**
    * 删除对象前的操作
    * @param t
    * @return 返回false终止删除
    */
  def onBeforeDelete(t:T):Boolean

  def list(c:Criteria, p: Pagination):util.List[T]

  def list(q:Query, p:Pagination):util.List[T]

  /**
    * 更新指定的属性
    * @param id
    * @param field
    * @param value
    */
  def modifyField(id:String, field:String, value:String):Unit

  def count:Long

  def count(criteria: Criteria):Long
}
