package com.muzlive.kitpage.kitpage.config.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

	public JwtAuthenticationException(CommonException cause) {
		super(cause.getMessage(), cause);
	}

	public CommonException getWrappedException() {
		return (CommonException) getCause();
	}
}
