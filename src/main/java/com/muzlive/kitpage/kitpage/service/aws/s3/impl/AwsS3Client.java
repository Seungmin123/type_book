package com.muzlive.kitpage.kitpage.service.aws.s3.impl;

import com.muzlive.kitpage.kitpage.service.aws.s3.StorageClient;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component("awsS3Client")
@RequiredArgsConstructor
public class AwsS3Client implements StorageClient {

	private final S3Client s3Client;

	@Override
	public void putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
		s3Client.putObject(putObjectRequest, requestBody);
	}

	@Override
	public InputStream getObject(GetObjectRequest getObjectRequest) {
		return s3Client.getObject(getObjectRequest);
	}

	@Override
	public void copyObject(CopyObjectRequest copyObjectRequest) {
		s3Client.copyObject(copyObjectRequest);
	}
}
