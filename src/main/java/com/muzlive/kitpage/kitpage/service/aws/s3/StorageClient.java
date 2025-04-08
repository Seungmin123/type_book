package com.muzlive.kitpage.kitpage.service.aws.s3;

import java.io.InputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public interface StorageClient {

	void putObject(PutObjectRequest putObjectRequest, RequestBody requestBody);

	InputStream getObject(GetObjectRequest getObjectRequest);

	void copyObject(CopyObjectRequest copyObjectRequest);

}
