package com.muzlive.kitpage.kitpage.config.transfer;

import com.muzlive.kitpage.kitpage.config.transfer.domain.KihnoV1Domain;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KihnoV2Domain;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KittorDomain;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.kittor")
public class KittorConfig {

    private String domain;

    @Bean(name = "kittorDomain")
    public KittorDomain getKittorDomain() {
        return new KittorDomain(this.domain);
    }
}
