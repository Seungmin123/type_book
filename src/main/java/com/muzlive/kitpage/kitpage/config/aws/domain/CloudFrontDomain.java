package com.muzlive.kitpage.kitpage.config.aws.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;

@Getter
@Setter
@AllArgsConstructor
public class CloudFrontDomain {

    private String domain;

    private String s3configPath;

    private String keyPairId;

    private CloudFrontUtilities cloudFrontUtilities;
}
