package com.alpha.rider.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpha.rider.responsedto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RiderNotFoundException.class)
	public ResponseStructure<String> handleRiderNotFoundException(){
		ResponseStructure<String> rs= new ResponseStructure<String>();
		rs.setStatuscode(HttpStatus.NOT_FOUND.value());
		rs.setMessage("RIDER NOT FOUND");
		rs.setData(null);
		return rs;
		
	}
	

}
