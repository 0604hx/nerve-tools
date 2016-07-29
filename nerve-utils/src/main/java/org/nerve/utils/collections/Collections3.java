/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.nerve.utils.collections;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.nerve.utils.reflection.ReflectionUtils;

import java.util.*;

/**
 * Collections工具集.
 * 在JDK的Collections和Guava的Collections2后, 命名为Collections3.
 */
public class Collections3 {

	/**
	 * 提取集合中的对象的两个属性(通过Getter函数), 组合成Map.
	 * 
	 * @param collection 来源集合.
	 * @param keyPropertyName 要提取为Map中的Key值的属性名.
	 * @param valuePropertyName 要提取为Map中的Value值的属性名.
	 *                          @return 结果
	 */
    public static Map extractToMap(final Collection collection, final String keyPropertyName,
			final String valuePropertyName) {
		Map map = new HashMap(collection.size());

		try {
			for (Object obj : collection) {
				map.put(PropertyUtils.getProperty(obj, keyPropertyName),
						PropertyUtils.getProperty(obj, valuePropertyName));
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}

		return map;
	}

	/**
	 * 提取集合中的对象的一个属性(通过Getter函数), 组合成List.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 *                     @return 结果
	 */
    public static List extractToList(final Collection collection, final String propertyName) {
		List list = new ArrayList(collection.size());

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
	 * 提取集合中的对象的一个属性(通过Getter函数), 组合成由分割符分隔的字符串.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param separator 分隔符.
	 *                  @return 结果
	 */
    public static String extractToString(final Collection collection, final String propertyName, final String separator) {
		List list = extractToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
	 * @param collection    集合
	 * @param separator     分隔符
	 * @return              结果
	 */
	public static String convertToString(@SuppressWarnings("rawtypes") final Collection collection, final String separator) {
		return StringUtils.join(collection, separator);
	}


	/**
	 * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
	 * @param collection    集合
	 * @param prefix        前缀
	 * @param postfix       后缀
	 * @return              字符串
	 */
	public static String convertToString(@SuppressWarnings("rawtypes") final Collection collection, final String prefix, final String postfix) {
		StringBuilder builder = new StringBuilder();
		for (Object o : collection) {
			builder.append(prefix).append(o).append(postfix);
		}
		return builder.toString();
	}

    public static boolean isEmpty(Collection collection) {
		return (collection == null || collection.isEmpty());
	}

	public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

	/**
	 * 取得Collection的第一个元素，如果collection为空返回null.
	 * @param collection    集合
	 * @param <T>           元素类型
	 * @return              返回第一个元素
	 */
	public static <T> T getFirst(Collection<T> collection) {
		if (isEmpty(collection)) {
			return null;
		}

		return collection.iterator().next();
	}

	/**
	 *  获取Collection的最后一个元素 ，如果collection为空返回null.
	 * @param collection    集合
	 * @param <T>           元素类型
	 * @return              返回最后一个元素
	 */
	public static <T> T getLast(Collection<T> collection) {
		if (isEmpty(collection)) {
			return null;
		}

		//当类型为List时，直接取得最后一个元素 。
		if (collection instanceof List) {
			List<T> list = (List<T>) collection;
			return list.get(list.size() - 1);
		}

		//其他类型通过iterator滚动到最后一个元素.
		Iterator<T> iterator = collection.iterator();
		while (true) {
			T current = iterator.next();
			if (!iterator.hasNext()) {
				return current;
			}
		}
	}

	/**
	 * 返回a+b的新List
	 * @param a         集合A
	 * @param b         集合B
	 * @param <T>       集合元素类型
	 * @return          合并后的集合
	 */
	public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
		List<T> result = new ArrayList<T>(a);
		result.addAll(b);
		return result;
	}

	/**
	 * 返回a-b的新List.
	 * @param a     母集合
	 * @param b     子集合（将要被删除的）
	 * @param <T>   类型
	 * @return      a-b后的集合
	 */
	public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
		List<T> list = new ArrayList<T>(a);
		for (T element : b) {
			list.remove(element);
		}

		return list;
	}

    /**
     * 获取list2在list1中的无重复补集
     * 注意：需要重写List集合中对象的hashcode和equals方法
     * A={2,3} comple B={1,1,2,6} = C={1,6}
     * @param a     集合a
     * @param b     集合b
     * @param <T>       泛型
     * @return      b在a中的无重复补集
     */
	public static <T> List<T>  comple(Collection<T> a,Collection<T> b){
        Set<T> set = new LinkedHashSet();
        set.addAll(a);
        set.removeAll(intersection(a, b));
        return new ArrayList(set);
    }

	/**
	 * 返回a与b的交集的新List.
	 * @param a     集合a
	 * @param b     集合b
	 * @param <T>   类型
	 * @return      a与b的交集
	 */
	public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
		List<T> list = new ArrayList<T>();

		for (T element : a) {
			if (b.contains(element)) {
				list.add(element);
			}
		}
		return list;
	}

    //返回a与b的无重复并集的新List.
    public static <T> List<T>  aggregate(Collection<T> a,Collection<T> b){
        List<T> list = new ArrayList<T>();
        if (a != null) {
            Iterator it = a.iterator();
            while (it.hasNext()) {
                T o = (T)it.next();
                if (!list.contains(o)) {
                    list.add(o);
                }
            }
        }
        if (b != null) {
            Iterator it = b.iterator();
            while (it.hasNext()) {
                T o = (T) it.next();
                if (!list.contains(o))
                    list.add(o);
            }
        }
        return list;
    }
}