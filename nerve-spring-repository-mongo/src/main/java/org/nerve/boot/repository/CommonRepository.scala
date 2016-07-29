package org.nerve.boot.repository

import java.util

import com.mongodb.{CommandResult, WriteResult}
import org.nerve.boot.repository.tools.Pagination
import org.springframework.data.domain.{Page, Pageable, Sort}
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.NoRepositoryBean

/**
  * 通用的Repository
  * Created by zengxm on 2016/4/14 0014.
  */
@NoRepositoryBean
trait CommonRepository[T,ID<:java.io.Serializable] extends MongoRepository[T,ID]{
  def find(query:Query, p: Pageable):Page[T]
  def find(criteria: Criteria, p: Pageable):Page[T]

  /**
    * 获取第一个，默认按照ID desc排序
    * @param criteria
    * @return
    */
  def findOne(criteria: Criteria):T
  def findOne(query: Query):T

  /**
    * 根据Criteria来进行分页查询
    * @param query
    * @param p
    * @return
    */
  def find(query:Query, p: Pagination):util.List[T]

  /**
    * @param query
    * @return
    */
  def find(query: Query):util.List[T]

  /**
    * 根据Query来进行分页查询
    * @param criteria
    * @param p
    * @return
    */
  def find(criteria: Criteria, p: Pagination):util.List[T]

  /**
    * 根据Criteria来删除执行的记录
    * @param criteria
    */
  def delete(criteria: Criteria):Long

  /**
    * 根据Criteria来统计
    * @param criteria
    */
  def count(criteria: Criteria):Long

  def updateFirst(query: Query,update:Update):WriteResult

  def update(query: Query, update: Update):WriteResult

  /**
    * 对某个字段进行分组统计
    * @param groupField     需要groupby的字段
    * @param criteria       遍历条件
    * @param fields         需要抽取出来的字段（key=原字段，value=抽取后的字段名，不能包含.）
    * @param sort           结果的排序方式
    * @param sortField      排序的字段（分组结果中的其中一个字段）
    * @return
    */
  def groupBy(groupField: String, criteria: Criteria,fields:Map[String,String]=null,
              sort:Sort.Direction=Sort.Direction.ASC,sortField:String=null):util.List[_]

  /**
    * 聚合操作
    * @param aggregation
    */
  def aggregate[M<:java.lang.Object](aggregation: Aggregation,clazz:Class[M]):util.List[M]

  /**
    * 获取表名
    * @return
    */
  def getCollectionName:String

  def execCommand(json:String):CommandResult

  /**
    * 获取表的状态信息：execCommand("{collstats:'colName'})
    *
    * 返回结果样例：
    * {
      "ns" : "mongos.mongo_document",
      "count" : NumberInt(22821684),
      "size" : NumberLong(9328350918),
      "avgObjSize" : NumberInt(408),
      "storageSize" : NumberLong(2417422336),
    }
    */
  def getCollectionStats:CommandResult
}
