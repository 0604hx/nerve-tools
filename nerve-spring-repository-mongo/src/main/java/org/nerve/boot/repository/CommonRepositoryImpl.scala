package org.nerve.boot.repository

import java.util

import com.mongodb.{CommandResult, WriteResult}
import org.nerve.boot.repository.tools.Pagination
import org.springframework.data.domain.{Page, PageImpl, Pageable, Sort}
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository

/**
  *
  * 以下是java版本的代码（用于参考）
  * =================================================================
  * package com.zeus.dpos.repository.commons;

    import com.zeus.dpos.repository.tools.Pagination;
    import org.springframework.boot.autoconfigure.AutoConfigureOrder;
    import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageImpl;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.mongodb.core.MongoOperations;
    import org.springframework.data.mongodb.core.query.Criteria;
    import org.springframework.data.mongodb.core.query.Query;
    import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
    import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
    import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
    import org.springframework.stereotype.Repository;

    import java.io.Serializable;
    import java.lang.reflect.ParameterizedType;
    import java.util.List;

    /**
      * com.zeus.dpos.repository.commons
      * Created by zengxm on 2016/4/14 0014.
      */
    public class CommonMongoRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T,ID> implements CommonRepository<T,ID> {
      protected final MongoOperations mongoTemplate;

      protected final MongoEntityInformation<T, ID> entityInformation;

      public CommonMongoRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);

        this.mongoTemplate=mongoOperations;
        this.entityInformation = metadata;
      }

      protected Class<T> getEntityClass(){
        return entityInformation.getJavaType();
      }

      @Override
      public Page<T> find(Query query, Pageable p) {
        long total=mongoTemplate.count(query, getEntityClass());
        List<T> list=mongoTemplate.find(query.with(p), getEntityClass());

        return new PageImpl<T>(list, p, total);
      }

      @Override
      public Page<T> find(Criteria criteria, Pageable p) {
        return find(new Query(criteria), p);
      }

      @Override
      public List<T> find(Query query, Pagination p) {
        long total=mongoTemplate.count(query, getEntityClass());
        p.setTotal(total);
        query.skip((p.getPage()-1)*p.getPageSize())
            .limit(p.getPageSize())
        ;

        return mongoTemplate.find(query, getEntityClass());
      }

      @Override
      public List<T> find(Criteria criteria, Pagination p) {
        return find(new Query(criteria), p);
      }

      @Override
      public void delete(Criteria criteria) {
        mongoTemplate.remove(new Query(criteria), getEntityClass());
      }
    }
  *
  * =================================================================
  * Created by zengxm on 2016/4/14 0014.
  */
class CommonRepositoryImpl[T, ID<:java.io.Serializable](matedata:MongoEntityInformation[T,ID],mongoOp:MongoOperations)
  extends SimpleMongoRepository[T,ID](matedata, mongoOp)
    with CommonRepository[T,ID]
  {

    override def find(query: Query, p: Pageable): Page[T] = {
      val total=mongoOp.count(query, matedata.getJavaType)
      val list=mongoOp.find(query.`with`(p), matedata.getJavaType)

      new PageImpl[T](list, p, total)
    }

    override def find(criteria: Criteria, p: Pageable): Page[T] = find(new Query(criteria), p)

    /**
      * 根据Criteria来删除执行的记录
      * @param criteria
      */
    override def delete(criteria: Criteria): Long ={
      val result=mongoOp.remove(new Query(criteria), matedata.getJavaType)
      result.getN
    }

    /**
      * 根据Criteria来进行分页查询
      * @param query
      * @param p
      * @return
      */
    override def find(query: Query, p: Pagination): util.List[T] = {
      val total=mongoOp.count(query, matedata.getJavaType)
      p.setTotal(total)

      query.skip((p.getPage - 1) * p.getPageSize).limit(p.getPageSize)

      mongoOp.find(query, matedata.getJavaType)
    }




//    def delete(entity: T): Unit = {
//      if(entity.isInstanceOf[TrashEntity]){
//        val t=entity.asInstanceOf[TrashEntity]
//        t.setTrash(true)
//
//        save(entity)
//      }else
//        super.delete(entity)
//    }

  /**
    * @param query
    * @return
    */
  override def find(query: Query): util.List[T] = mongoOp.find(query, matedata.getJavaType)

  /**
      * 根据Query来进行分页查询
      * @param criteria
      * @param p
      * @return
      */
    override def find(criteria: Criteria, p: Pagination): util.List[T] = find(new Query(criteria),p)

    /**
      * 获取第一个，默认按照ID desc排序
      * @param criteria
      * @return
      */
    override def findOne(criteria: Criteria): T = findOne(new Query(criteria))

    override def findOne(query: Query): T = {
      if(query.getSortObject==null)
        query.`with`(new Sort(Sort.Direction.DESC, "_id"))
      mongoOp.findOne(query,matedata.getJavaType)
    }

  /**
    * 根据Criteria来统计
    * @param criteria
    */
  override def count(criteria: Criteria): Long = mongoOp.count(new Query(criteria), matedata.getJavaType)

  override def update(query: Query, update: Update): WriteResult = mongoOp.updateMulti(query, update, matedata.getJavaType)

  override def updateFirst(query: Query, update: Update): WriteResult = mongoOp.updateFirst(query, update, matedata.getJavaType)

  /**
    * 对某个字段进行分组统计
    * @param groupField     需要groupby的字段
    * @param criteria       遍历条件
    * @param fields         需要抽取出来的字段（key=原字段，value=抽取后的字段名，不能包含.）
    * @param sort           结果的排序方式
    * @param sortField      排序的字段（分组结果中的其中一个字段）
    * @return
    */
  override def groupBy(groupField: String, criteria: Criteria,fields:Map[String,String]=null,
              sort:Sort.Direction=Sort.Direction.ASC,sortField:String=null):util.List[_]={
    val mactchF = Aggregation.`match`(if(criteria==null) new Criteria() else criteria)

    var group = Aggregation.group(groupField)
      .count().as("count")
    if(fields!=null && !fields.isEmpty)
      for (elem <- fields) {group=group.last(elem._1).as(elem._2)}
    else
      group=group.last(groupField).as("groupBy")


    val aggregation = Aggregation.newAggregation(
      mactchF,                                            //筛选条件
      group,                                              //分组规则
      Aggregation.sort(
        sort,                                             //排序方式
        if(sortField==null) "count" else sortField)  //排序的字段（分组结果中的其中一个字段）
    )

    val results = mongoOp.aggregate(aggregation, matedata.getJavaType, classOf[util.Map[_,_]])
    results.getMappedResults
  }

  /**
    * 聚合操作
    * @param aggregation
    */
  override def aggregate[M <: Object](aggregation: Aggregation, clazz: Class[M]): util.List[M] = {
    val results=mongoOp.aggregate(aggregation, matedata.getJavaType, clazz)
    results.getMappedResults
  }

  /**
    * 获取表名
    * @return
    */
  override def getCollectionName: String = matedata.getCollectionName

  override def execCommand(json: String): CommandResult = mongoOp.executeCommand(json)

  /**
    * 获取表的状态信息：execCommand("{collstats:'colName'})
    *  *
    * 返回结果样例：
    * {
      "ns" : "mongos.mongo_document",
      "count" : NumberInt(22821684),
      "size" : NumberLong(9328350918),
      "avgObjSize" : NumberInt(408),
      "storageSize" : NumberLong(2417422336),
    }
    */
  override def getCollectionStats:CommandResult= execCommand("{collstats:'"+getCollectionName+"'}")
}
