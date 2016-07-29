package org.nerve.boot.repository.tools;

import java.lang.reflect.ParameterizedType;

/**
 * 获取泛型class的工具
 * Created by zengxm on 2016/3/1 0001.
 */
public class GenericItem<T extends Object> {
	private Class<T> genericClass;

	public Class<T> getGenericClass(){
		if(genericClass == null){
			genericClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return genericClass;
	}

	public String getGenericClassName(){
		return getGenericClass().getName();
	}

	public T getGenericInstance() throws IllegalAccessException, InstantiationException {
		return getGenericClass().newInstance();
	}
}
