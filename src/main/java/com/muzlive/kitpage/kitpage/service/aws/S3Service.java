package com.muzlive.kitpage.kitpage.service.aws;

import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Service
public class S3Service {

	@Value("${spring.aws.s3.bucket}")
	private String BUCKET;

	private final S3Client s3Client;

	private final AesSecurityProvider aesSecurityProvider;

	public void uploadFile(String key, MultipartFile multipartFile) throws Exception {
		this.uploadFile(BUCKET, key, multipartFile.getBytes());
	}

	public void uploadDecryptFile(String key, MultipartFile multipartFile) throws Exception {
		this.uploadDecryptFile(BUCKET, key, multipartFile.getBytes());
	}

	public void uploadFile(String key, byte[] bytes) throws Exception {
		this.uploadFile(BUCKET, key, bytes);
	}

	private void uploadFile(String bucket, String key, byte[] bytes) throws Exception {

		byte[] encryptContent = aesSecurityProvider.encrypt(bytes);
		InputStream encryptInputStream = new ByteArrayInputStream(encryptContent);

		s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
			RequestBody.fromInputStream(encryptInputStream, encryptContent.length));
	}

	public void uploadDecryptFile(String key, byte[] bytes) throws Exception {
		this.uploadDecryptFile(BUCKET, key, bytes);
	}

	private void uploadDecryptFile(String bucket, String key, byte[] bytes) throws Exception {
		InputStream encryptInputStream = new ByteArrayInputStream(bytes);

		s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
			RequestBody.fromInputStream(encryptInputStream, bytes.length));
	}

	public InputStream downloadFile(String key) throws Exception {
		return this.downloadFile(BUCKET, key);
	}

	public InputStream downloadFile(String bucket, String key) throws Exception {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.build();

		return s3Client.getObject(getObjectRequest);
	}

	public void copyObject(String sourceKey, String destinationKey) {
		this.copyObject(BUCKET, sourceKey, BUCKET, destinationKey);
	}

	public void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
		CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
			.copySourceIfMatch(sourceBucket + "/" + sourceKey)
			.destinationBucket(destinationBucket)
			.destinationKey(destinationKey)
			.build();

		CopyObjectResponse response = s3Client.copyObject(copyObjectRequest);
	}

}
