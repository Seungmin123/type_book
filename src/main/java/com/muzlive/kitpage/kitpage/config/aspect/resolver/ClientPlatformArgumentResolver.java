package com.muzlive.kitpage.kitpage.config.aspect.resolver;

import com.muzlive.kitpage.kitpage.config.aspect.ClientPlatform;
import com.muzlive.kitpage.kitpage.utils.constants.HeaderConstants;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ClientPlatformArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ClientPlatform.class) &&
			parameter.getParameterType().equals(ClientPlatformType.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		return resolveClientPlatformType(request);
	}

	private ClientPlatformType resolveClientPlatformType(HttpServletRequest request) {
		String platformCode = request.getHeader(HeaderConstants.OS);
		if (platformCode == null || platformCode.isBlank()) {
			return ClientPlatformType.ANONYMOUS;
		}
		return ClientPlatformType.getClientPlatformTypeByCode(platformCode.toLowerCase());
	}
}
