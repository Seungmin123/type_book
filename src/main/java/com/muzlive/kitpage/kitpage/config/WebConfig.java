package com.muzlive.kitpage.kitpage.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzlive.kitpage.kitpage.config.jwt.CurrentTokenArgumentResolver;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean("jsonTo")
    public ObjectMapper setObjectmapper(){
        return new Jackson2ObjectMapperBuilder().build().
                setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver sessionLocaleResolver = new AcceptHeaderLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.KOREA); // 기본 언어 설정
        return sessionLocaleResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(10);
        return messageSource;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentTokenArgumentResolver(jwtTokenProvider));
    }
}
