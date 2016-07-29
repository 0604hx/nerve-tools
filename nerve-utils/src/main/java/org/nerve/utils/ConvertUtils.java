/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.nerve.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.nerve.utils.reflection.ReflectionUtils;

import java.util.*;

/**
 * 转换工具类.
 */
public class ConvertUtils extends org.apache.commons.beanutils.ConvertUtils{

	static {
		registerDateConverter();
	}

	/**
	 * 提取集合中的对象的属性(通过getter函数), 组合成List.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List convertElementPropertyToList(final Collection collection, final String propertyName) {
		List list = new ArrayList();

		try {
			for (Object obj : collection) {
				list.add(PropertyUtils.getProperty(obj, propertyName));
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}

		return list;
	}

	/**
	 * 提取集合中对象的属性，放置到新的javaBean，集合对象跟javabean之间的属性通过properties来指定
	 * 如集合对象有属性：name，title
	 * javabean有属性：name，value
	 * 则properties为
	 * {
	 *     name:name,
	 *     title:value
	 * }
	 * 时，集合对象中的name就复制到javabean的name，title复制到value
	 * @param collection
	 * @param properties
	 * @return
	 */
	public static List convertElementPropertyToBeanList(final Collection collection, final Map<String, String> properties, Class<?> toType){
		List list = new ArrayList();

		try {
			for (Object obj : collection) {
				Object bean = toType.newInstance();
				for(String key:properties.keySet()){
					PropertyUtils.setProperty(bean, properties.get(key), PropertyUtils.getProperty(obj, key));
				}
				list.add(bean);
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}

		return list;
	}

	/**
	 * 提取集合中的对象的属性(通过getter函数), 组合成由分割符分隔的字符串.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param separator 分隔符.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String convertElementPropertyToString(final Collection collection, final String propertyName,
			final String separator) {
		List list = convertElementPropertyToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * 转换字符串到相应类型.
	 * 
	 * @param value 待转换的字符串.
	 * @param toType 转换目标类型.
	 */
	public static Object convertStringToObject(String value, Class<?> toType) {
		try {
			return org.apache.commons.beanutils.ConvertUtils.convert(value, toType);
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
	}
	
	/**
	 * 基于Apache BeanUtils转换字符串到相应类型.
	 * 
	 * @param value 待转换的字符串.
	 * @param toType 转换目标类型.
	 */
	public static Object convertToObject(String value, Class<?> toType) {
		try {
			return convert(value, toType);
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 转换字符串数组到相应类型.
	 * 
	 * @param values 待转换的字符串.
	 * @param toType 转换目标类型.
	 */
	public static Object convertToObject(String[] values,Class<?> toType) {
		try {
			return convert(values, toType);
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
	}
	

	/**
	 * 定义日期Converter的格式: yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
	 */
	private static void registerDateConverter() {
		DateConverter dc = new DateConverter();
		dc.setUseLocaleFormat(true);
		dc.setPatterns(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
		org.apache.commons.beanutils.ConvertUtils.register(dc, Date.class);
	}
}
