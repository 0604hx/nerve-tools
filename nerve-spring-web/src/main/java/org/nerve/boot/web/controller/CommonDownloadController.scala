package org.nerve.boot.web.controller

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
  * Created by zengxm on 2016/6/17.
  */
abstract class CommonDownloadController extends BaseController{
  /**
    * 下载文件到浏览器
    * @param file
    * @param fileName
    * @param onDone
    */
  def downloadFile(request:HttpServletRequest, response:HttpServletResponse,file:File, fileName:String,onDone:()=>Unit=()=>Unit):Unit={
    var bis:BufferedInputStream=null;
    var bos:BufferedOutputStream=null;

    try{
      //设置 response 的头信息
      response.setContentType("text/html;charset=utf-8");
      request.setCharacterEncoding("UTF-8");
      response.setContentType("application/x-msdownload;");

      val fName=if(fileName == null) file.getName else fileName

      response.setHeader("Content-disposition", "attachment; filename="+new String(fName.getBytes("utf-8"), "ISO-8859-1"))
      println(toJson(response.getHeader("Content-disposition")))

      //获取文件以及 输入输出流
      bis=new BufferedInputStream(new FileInputStream(file));
      bos=new BufferedOutputStream(response.getOutputStream());

      val blen=1024
      //将文件的 字节传递出去
      val buff = new Array[Byte](blen)
      var bytesRead =bis.read(buff, 0, buff.length)
      while (bytesRead != -1 ) {
        bos.write(buff, 0, bytesRead)
        bytesRead = bis.read(buff, 0, bytesRead)
      }
      println("传输完成")
      bos.flush()
    }catch {
      case e:Exception=>{throw e}
    }finally {
      if (bis != null)
        bis.close();
      if (bos != null)
        bos.close();
      onDone()
    }
  }
}
