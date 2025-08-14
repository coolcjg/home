package com.cjg.home.exception;




import com.cjg.home.code.ResultCode;
import lombok.Getter;


@Getter
public class CustomException extends RuntimeException {
	
	private final ResultCode resultCode;
	
	public CustomException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}
