/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.nerve.utils.collections;
import java.util.List;

/**
 * 列表List工具类，用于实现一些list的常用操作
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2011-7-22 上午12:37:10
 */
public class ListUtils {

    private static final String defaultSeparator = ",";

    /**
     * 判断list是否为空或大小为0
     * 
     * @param sourceList
     * @return 若list为null或长度为0, 返回true; 否则返回false.
     * 
     * <pre>
     *      isEmpty(null)   =   true;
     *      isEmpty({})     =   true;
     *      isEmpty({1})    =   false;
     * </pre>
     */
    public static boolean isEmpty(List<?> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

    /**
     * 将list中所有元素以默认分隔符拼接返回，默认分隔符为","
     * 
     * @param list
     * @return list中所有元素以默认分隔符拼接返回。若list为空或长度为0返回""
     * 
     * <pre>
     *      join(null)      =   "";
     *      join({})        =   "";
     *      join({a,b})     =   "a,b";
     * </pre>
     */
    public static String join(List<String> list) {
        return join(list, getDefaultSeparator());
    }

    /**
     * 将list中所有元素以separator分隔符拼接返回
     * 
     * @param list
     * @param separator
     * @return list中所有元素以separator分隔符拼接返回。若list为空或长度为0返回""
     * 
     * <pre>
     *      join(null, "#")     =   "";
     *      join({}, "#")       =   "";
     *      join({a,b,c}, "")   =   "abc";
     *      join({a,b,c}, "#")  =   "a#b#c";
     * </pre>
     */
    public static String join(List<String> list, char separator) {
        return join(list, separator + "");
    }

    /**
     * 将list中所有元素以separator分隔符拼接返回，separator为空则采用默认分隔符","
     * 
     * @param list
     * @param separator
     * @return list中所有元素以separator分隔符拼接返回。若list为空或长度为0返回""
     * 
     * <pre>
     *      join(null, "#")     =   "";
     *      join({}, "#$")      =   "";
     *      join({a,b,c}, null) =   "a,b,c";
     *      join({a,b,c}, "")   =   "abc";
     *      join({a,b,c}, "#")  =   "a#b#c";
     *      join({a,b,c}, "#$") =   "a#$b#$c";
     * </pre>
     */
    public static String join(List<String> list, String separator) {
        if (isEmpty(list)) {
            return "";
        }
        if (separator == null) {
            separator = getDefaultSeparator();
        }

        StringBuilder joinStr = new StringBuilder();
        for (String entry : list) {
            joinStr.append(entry).append(separator);
        }

        int lastIndexOfPosi = joinStr.lastIndexOf(separator);
        if (lastIndexOfPosi != -1 && lastIndexOfPosi == (joinStr.length() - separator.length())) {
            return joinStr.substring(0, joinStr.length() - separator.length());
        }
        return joinStr.toString();
    }

    /**
     * 向sourceList中新增不重复元素
     * 
     * @param <V>
     * @param sourceList
     * @param entry
     * @return 若entry在sourceList已经存在，返回false；否则新增并返回true 注意此函数不能保证源sourceList中元素不重复。
     */
    public static <V> boolean addDistinctEntry(List<V> sourceList, V entry) {
        return (sourceList != null && !sourceList.contains(entry)) ? sourceList.add(entry) : false;
    }

    /**
     * 向sourceList中插入包含在entryList而不包含在sourceList中的元素
     * 
     * @param <V>
     * @param sourceList
     * @param entryList
     * @return 像sourceList插入的元素个数 注意此函数不能保证源sourceList中元素不重复。
     */
    public static <V> int addDistinctList(List<V> sourceList, List<V> entryList) {
        if (sourceList == null || isEmpty(entryList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        for (V entry : entryList)
            if (!sourceList.contains(entry)) {
                sourceList.add(entry);
            }

        return sourceList.size() - sourceCount;
    }

    /**
     * 去除list中重复的元素
     * 
     * @param <V>
     * @param sourceList
     * @return 去除元素的个数
     */
    public static <V> int distinctList(List<V> sourceList) {
        if (isEmpty(sourceList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        int sourceListSize = sourceList.size();
        for (int i = 0; i < sourceListSize; i++)
            for (int j = (i + 1); j < sourceListSize; j++) {
                if (sourceList.get(i).equals(sourceList.get(j))) {
                    sourceList.remove(j);
                    sourceListSize = sourceList.size();
                    j--;
                }
            }

        return sourceCount - sourceList.size();
    }

    public static String getDefaultSeparator() {
        return defaultSeparator;
    }

    /**
     * 向list中新增value
     * 
     * @param sourceList
     * @param value
     * @return 若add成功，返回true，否则返回false
     *         <ul>
     *         <li>若sourceList为null，返回false</li>
     *         <li>若value为null，返回false</li>
     *         <li>{@link List#add(Object)} 异常返回false</li>
     *         <li>以上皆不满足，返回true</li>
     *         </ul>
     */
    public static <V> boolean addListNotNullValue(List<V> sourceList, V value) {
        return (sourceList != null && value != null) ? sourceList.add(value) : false;
    }

    /**
     * 参考{@link ArrayUtils#getLast(Object[], Object, Object, boolean)} defaultValue为null, isCircle为true
     */
    @SuppressWarnings("unchecked")
    public static <V> V getLast(List<V> sourceList, V value) {
        return (V)ArrayUtils.getLast(sourceList.toArray(), value, true);
    }

    /**
     * 参考{@link ArrayUtils#getNext(Object[], Object, Object, boolean)} defaultValue为null, isCircle为true
     */
    @SuppressWarnings("unchecked")
    public static <V> V getNext(List<V> sourceList, V value) {
        return (V)ArrayUtils.getNext(sourceList.toArray(), value, true);
    }
}