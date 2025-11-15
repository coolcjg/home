package com.cjg.home.exception;




import com.cjg.home.code.ResultCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


@Getter
public class CustomAuthException extends AuthenticationException {

	private final ResultCode resultCode;

	public CustomAuthException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}
