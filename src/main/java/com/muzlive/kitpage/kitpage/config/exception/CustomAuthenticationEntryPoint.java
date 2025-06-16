package com.muzlive.kitpage.kitpage.config.exception;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		String responseBody;

		if (authException instanceof JwtAuthenticationException) {
			JwtAuthenticationException jwtEx = (JwtAuthenticationException) authException;
			CommonException wrapped = jwtEx.getWrappedException();
			response.setStatus(wrapped.getStatus().value());
			responseBody = ErrorResponseBuilder.build(wrapped);
		} else {
			responseBody = ErrorResponseBuilder.buildFallback();
		}

		response.getWriter().write(responseBody);
	}
}
