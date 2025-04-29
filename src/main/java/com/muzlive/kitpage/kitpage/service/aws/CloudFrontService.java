package com.muzlive.kitpage.kitpage.service.aws;

import com.muzlive.kitpage.kitpage.config.aws.domain.CloudFrontDomain;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.internal.utils.SigningUtils;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class CloudFrontService {

	private final CloudFrontDomain cloudFrontDomain;

	public String getSignedUrl(String key) throws Exception {
		CannedSignerRequest request = createRequestForCannedPolicy(key);
		return cloudFrontDomain.getCloudFrontUtilities().getSignedUrlWithCannedPolicy(request).url();
	}

	public byte[] getCFImageByKey(String key) throws Exception {
		return this.downloadFile(this.getSignedUrl(key));
	}

	private byte[] downloadFile(String fileUrl) throws Exception {
		URL url = new URL(fileUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try (InputStream inputStream = connection.getInputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			return byteArrayOutputStream.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	private CannedSignerRequest createRequestForCannedPolicy(String key) throws Exception {
		String cloudFrontUrl = cloudFrontDomain.getDomain() + "/" + key;
		Instant expirationDate = Instant.now().plus(1, ChronoUnit.DAYS);

		Path path = Paths.get(cloudFrontDomain.getS3configPath());

		return CannedSignerRequest.builder()
			.resourceUrl(cloudFrontUrl)
			.privateKey(SigningUtils.loadPrivateKey(path))
			.keyPairId(cloudFrontDomain.getKeyPairId())
			.expirationDate(expirationDate)
			.build();
	}

}
