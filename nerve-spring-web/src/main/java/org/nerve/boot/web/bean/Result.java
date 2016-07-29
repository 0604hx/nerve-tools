package org.nerve.boot.web.bean;

import java.util.List;

/**
 * Result.java
 * @author seven
 * last edit by:udk
 * 2010-12-5
 */
public class Result {
	/** 总记录数 */
	private int total;
	/** 分页结果 */
	private List<?> root;
	/** 查询结果 */
	private boolean success = true;
	/** 额外的信息 */
	private Object data;
	private String message;
	public Result(){}

	public Result(boolean success, String message){
		this.success = success;
		this.message = message;
	}
	public Result(boolean success, String message, Object data){
		this.success = success;
		this.message = message;
		this.data =data;
	}

	public void error(Exception e){
		this.success = false;
		if(e.getCause() == null)
			message = e.getMessage();
		else
			message = e.getCause().getMessage();
	}
	
	public Result(List<?> root){
		this.root=root;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<?> getRoot() {
		return root;
	}
	public Result setRoot(List<?> root) {
		this.root = root;
		return this;
	}
	public int getTotal() {
		return total;
	}
	public Result setTotal(int total) {
		this.total = total;
		return this;
	}
	public Result setSuccess(boolean success) {
		this.success = success;
		return this;
	}
	public boolean isSuccess() {
		return success;
	}
	public Object getData() {
		return data;
	}
	public Result setData(Object data) {
		this.data = data;
		return this;
	}
}
