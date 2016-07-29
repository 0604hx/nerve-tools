package org.nerve.boot.repository.tools

import scala.beans.BeanProperty

/**
  * 分页组件，兼容JeasyUI（rows为easyui传递的参数）
  * Created by zengxm on 2016/4/14 0014.
  */
class Pagination extends Serializable{
  @BeanProperty var page:Int=1
  var pageSize:Int=Pagination.DEFAULT_PAGESIZE
  var total:Long=0
  @BeanProperty var maxPage:Int=0
  var rows:Int=0

  def this(ps:Int)={
    this()
    this.setPageSize(ps)
  }

  @Override def setRows(r:Int)={
    this.rows=r;
    setPageSize(r);
  }
  @Override def setTotal(r:Long)={
    this.total=r;
    maxPage = (total - 1).toInt / this.pageSize + 1
  }

  def getTotal=total


  @Override
  def setPageSize(r:Int)={
    pageSize = if(r>0) r else Pagination.DEFAULT_PAGESIZE
  }

  def getPageSize=pageSize

  @Override
  def total_(t:Long)={
    this.total=t;
    maxPage = (total - 1).toInt / this.pageSize + 1
  }
}

object Pagination{
  val DEFAULT_PAGESIZE=20
}
