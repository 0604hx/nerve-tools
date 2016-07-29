package org.nerve.boot.repository.bean;

/**
 * Created by zengxm on 2016/2/25 0025.
 */
public final class Status {
	public static final String PENDING="PENDING";        //未初始化的状态
	public static final String WAITING="WAITING";       //待命中（可以随时进行工作）
	public static final String WORKING="WORKING";       //工作中
	public static final String BUSY="BUSY";     //繁忙中
	public static final String STOPED="STOPED";     //已停止
	public static final String UNABLE_TO_CONNECT="UNABLE_CONNECT";  //无法链接
	public static final String DONE="DONE";//已完成
	public static final String FAILED="FAILED";//失败
}
