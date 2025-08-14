package com.cjg.home.exception;




import com.cjg.home.code.ResultCode;
import lombok.Getter;


@Getter
public class CustomViewException extends RuntimeException {

	private final ResultCode resultCode;

	public CustomViewException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}
