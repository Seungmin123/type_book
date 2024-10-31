package com.muzlive.kitpage.kitpage.service.transfer.kihno;

import com.muzlive.kitpage.kitpage.config.transfer.domain.MuzDomain;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class MuzTransferService {

	@Value("${spring.aws.s3.bucket}")
	private String BUCKET;

	private final String VIDEO_ENCODING_URL = "/startEncodingVideo";

	private WebClient webClient;

	public MuzTransferService(WebClient.Builder builder, MuzDomain muzDomain) {
		this.webClient = builder.baseUrl(muzDomain.getDomain()).build();
	}

	public Map encodingVideo(String filePath) throws Exception {
		LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("path", filePath);
		multiValueMap.add("bucket_name", BUCKET);

		return webClient.get()
			.uri(urlBuilder -> urlBuilder.path(VIDEO_ENCODING_URL).queryParams(multiValueMap).build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(Map.class)
			.doOnError(e -> log.error(e.getMessage()))
			.block();
	}

}
