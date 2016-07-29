package org.nerve.boot.web.controller

import javax.servlet.http.HttpServletRequest

import org.nerve.boot.repository.IdEntity
import org.nerve.boot.repository.basic.CommonService
import org.nerve.boot.repository.tools.Pagination
import org.nerve.boot.web.bean.{Result, PageResult}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation._

/**
  * Created by zengxm on 2015/12/28 0028.
  */
abstract class AbstractController[T<:IdEntity,R<:CrudRepository[T,String],S<:CommonService[T,R]] extends CommonDownloadController{
  @Autowired var service:S = _

  @RequestMapping(Array(""))
  def index(map:ModelMap):String = {
    return view(INDEX)
  }

  @ResponseBody
  @RequestMapping(value=Array("/{id}"), method = Array(RequestMethod.GET))
  def get(@PathVariable("id")id:String):T = service.get(id)

  @ResponseBody
  @RequestMapping(Array("list"))
  def list(req:HttpServletRequest, p:Pagination):Any={
    val datas = service.list(buildMongoQueryFromRequest(req, DEFAULT_SEARCH_PREFIX), p)
    return new PageResult(p.total, datas)
  }

  @ResponseBody
  @RequestMapping(value=Array("add","edit"), method = Array(RequestMethod.POST))
  def add(t:T):Result={
    val re:Result = new Result()
    try{
      service.save(t)
    }catch {
      case e:Exception => re.error(e)
    }
    return re
  }

  @ResponseBody
  @RequestMapping(Array("delete"))
  def delete(@RequestParam("ids") ids:Array[String]):Result = {
    val re:Result = new Result()
    try{
      ids.foreach(service.delete(_))
    }catch {
      case e:Exception => re.error(e)
    }
    return re
  }

  @RequestMapping(Array("modify"))
  @RequestBody
  def modifyField(id:String, field:String, value:String):Result={
    val re:Result = new Result
    try{
      service.modifyField(id, field, value)
    }catch {
      case e:Exception => {
        re.error(e)
        e.printStackTrace()
      }
    }
    re
  }
}
