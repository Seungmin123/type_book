package com.muzlive.kitpage.kitpage.config.filter;

import com.muzlive.kitpage.kitpage.config.wrapper.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(1)
public class HttpRequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper((HttpServletRequest) servletRequest);

        RequestInfo info = new RequestInfo();
        Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headers = wrappedRequest.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                info.addHeader(headerName, headerValue);
            }
        }

        Enumeration<String> parameterNames = wrappedRequest.getParameterNames();
        if (parameterNames != null) {
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                String parameterValue = wrappedRequest.getParameter(parameterName);
                info.addParam(parameterName, parameterValue);
            }
        }

        String method = wrappedRequest.getMethod();
        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("PATCH")) {
            info.setBody(wrappedRequest.getBody());
        }

        info.setIp(wrappedRequest.getRemoteAddr());
        info.setMethod(wrappedRequest.getMethod());
        info.setPath(wrappedRequest.getRequestURI());
        if(!wrappedRequest.getRequestURI().equals("/actuator/health"))
            log.info("Request: " + info.toString());

        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    @Data
    private class RequestInfo implements Serializable {

        private static final long serialVersionUID = -308464796388529321L;

        private String ip;

        private String method;

        private String path;

        @Setter(AccessLevel.NONE)
        private Map<String, String> headers;

        @Setter(AccessLevel.NONE)
        private Map<String, String> params;

        private String body;

        protected void addHeader(String key, String value) {
            if (headers == null) {
                headers = new HashMap<String, String>();
            }
            headers.put(key, value);
        }

        protected void addParam(String key, String value) {
            if (params == null) {
                params = new HashMap<String, String>();
            }
            params.put(key, value);
        }
    }
}
