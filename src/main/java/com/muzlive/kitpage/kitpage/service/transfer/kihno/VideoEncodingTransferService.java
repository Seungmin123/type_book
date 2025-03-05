package com.muzlive.kitpage.kitpage.service.transfer.kihno;

import com.muzlive.kitpage.kitpage.config.transfer.domain.MuzDomain;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class VideoEncodingTransferService {

	@Value("${spring.aws.s3.bucket}")
	private String BUCKET;

	private final String VIDEO_ENCODING_URL = "/startEncodingVideo";

	private WebClient webClient;

	public VideoEncodingTransferService(WebClient.Builder builder, MuzDomain muzDomain) {
		this.webClient = builder.baseUrl(muzDomain.getVideoEncodingServer()).build();
	}

	public Map<String, Object> encodingVideo(String streamUrl) throws Exception {
		LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("path", streamUrl);
		multiValueMap.add("bucket_name", BUCKET);

		return webClient.get()
			.uri(urlBuilder -> urlBuilder.path(VIDEO_ENCODING_URL).queryParams(multiValueMap).build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
			.doOnError(e -> log.error(e.getMessage()))
			.block();
	}

}
