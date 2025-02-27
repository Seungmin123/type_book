package com.muzlive.kitpage.kitpage.config.transfer;

import com.muzlive.kitpage.kitpage.config.transfer.domain.PageDomain;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.page")
public class PageConfig {

    private String domain;

    @Bean(name = "pageDomain")
    public PageDomain getPageDomain() {
        return new PageDomain(this.domain);
    }

}
