package com.muzlive.kitpage.kitpage.config.interceptor;

import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.service.transfer.webhook.WebhookTransfer;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final List<String> ALLOWED_PATHS
            = Arrays.asList("/h2-console", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**", "/swagger-url", "/actuator/health"
                , "/v1"
    );

    private final JwtTokenProvider jwtTokenProvider;

    private final AesSecurityProvider aesSecurityProvider;

    private final WebhookTransfer webhookTransfer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

//        log.info("request url : {}", request.getRequestURL());
//        log.info("request remote addr : {}", getIp(request));

        String uri = request.getRequestURI();
        if (ALLOWED_PATHS.stream().anyMatch(uri::startsWith) || uri.endsWith("/health")) {
            return true;
        }

        String givenAccessToken = jwtTokenProvider.resolveToken(request);

        return jwtTokenProvider.validateAccessToken(givenAccessToken);
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}
