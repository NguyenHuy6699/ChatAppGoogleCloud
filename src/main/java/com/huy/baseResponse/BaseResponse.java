package com.huy.baseResponse;

import java.util.List;

public class BaseResponse<T> {
	private int code = -1;
	private boolean ok;
	private String message;
	private List<T> dataList;
	
	
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> data) {
		this.dataList = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public BaseResponse(boolean ok, String message, List<T> data) {
		super();
		this.ok = ok;
		this.message = message;
		this.dataList = data;
	}
	public BaseResponse(boolean ok, String message, List<T> data, int code) {
		super();
		this.ok = ok;
		this.message = message;
		this.dataList = data;
		this.code = code;
	}
	public BaseResponse(boolean ok, String message) {
		super();
		this.ok = ok;
		this.message = message;
	}
	public BaseResponse() {
		
	}
}
