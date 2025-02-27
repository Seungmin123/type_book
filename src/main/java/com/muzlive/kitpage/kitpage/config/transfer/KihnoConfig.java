package com.muzlive.kitpage.kitpage.config.transfer;

import com.muzlive.kitpage.kitpage.config.transfer.domain.KihnoV1Domain;
import com.muzlive.kitpage.kitpage.config.transfer.domain.KihnoV2Domain;
import com.muzlive.kitpage.kitpage.config.transfer.domain.MuzDomain;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.kihno")
public class KihnoConfig {

    private String v1Domain;

    private String v2Domain;

    private String videoEncodingServer;

    private String snsServer;

    @Bean(name = "kihnoV1Domain")
    public KihnoV1Domain getKihnoV1Domain() {
        return new KihnoV1Domain(this.v1Domain);
    }

    @Bean(name = "kihnoV2Domain")
    public KihnoV2Domain getKihnoV2Domain() {
        return new KihnoV2Domain(this.v2Domain);
    }

    @Bean(name = "muzDomain")
    public MuzDomain getMuzDomain() {
        return new MuzDomain(this.videoEncodingServer, this.snsServer);
    }
}
