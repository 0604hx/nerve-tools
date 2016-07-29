package org.nerve.boot.repository.config

import org.nerve.boot.repository.CommonRepositoryImpl
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
  * 配置默认的Repository实现类为CommonRepositoryImpl，代替SimpleMongoRepository
  *
  * 同时修改默认的Repository扫描目录为 com.zeus
  * Created by zengxm on 2016/4/14 0014.
  */
@AutoConfigurationPackage
@Configuration
@EnableMongoRepositories(repositoryBaseClass = classOf[CommonRepositoryImpl[_, _]], basePackages = Array("com.zeus"))
class Config {
}
