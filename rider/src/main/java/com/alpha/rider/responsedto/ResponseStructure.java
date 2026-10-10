package com.alpha.rider.responsedto;

import org.springframework.web.bind.annotation.RestControllerAdvice;

public class ResponseStructure<T> {
	private Integer statuscode;
	private String message;
	private T data;
	public Integer getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(Integer statuscode) {
		this.statuscode = statuscode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public ResponseStructure() {
		super();
	}
	public ResponseStructure(Integer statuscode, String message, T data) {
		super();
		this.statuscode = statuscode;
		this.message = message;
		this.data = data;
	}

}
