package org.nerve.boot.repository

import scala.beans.BeanProperty

/**
  * 带有trash字段的通用实体
  * Created by zengxm on 2016/1/19 0019.
  */
abstract class TrashEntity extends BaseEntity{
  @BeanProperty var trash:Boolean=_
}
