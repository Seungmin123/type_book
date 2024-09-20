package com.muzlive.kitpage.kitpage.config.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Configuration
public class SwaggerFilter extends OncePerRequestFilter {
    @Value("${security.access.iplist}")
    private String iplist;

    // private final FujiUtils fujiUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        boolean result = true;
//
//        if (request.getRequestURI().contains("swagger")) {
//            String[] splitAddress = iplist.split("[|]");
//            String realIp = fujiUtils.getIp(request);
//
//            result = false;
//
//            for(String accessIP : splitAddress){
//                if(accessIP.equals(realIp)){
//                    result = true;
//                }
//            }
//        }
//
//        if(result){
//            filterChain.doFilter(request,response);
//        }else {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN);
//        }

        filterChain.doFilter(request,response);
    }


}
