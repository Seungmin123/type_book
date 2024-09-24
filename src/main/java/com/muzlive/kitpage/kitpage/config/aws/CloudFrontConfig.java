package com.muzlive.kitpage.kitpage.config.aws;

import com.muzlive.kitpage.kitpage.config.aws.domain.CloudFrontDomain;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;

@Slf4j
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.aws.cloudfront")
public class CloudFrontConfig {

    private String domain;

    private String s3configPath;

    private String keyPairId;

    @Bean(name = "cloudFrontDomain")
    public CloudFrontDomain getCloudConfigDomain() {
        return new CloudFrontDomain(domain, s3configPath, keyPairId, CloudFrontUtilities.create());
    }
}
