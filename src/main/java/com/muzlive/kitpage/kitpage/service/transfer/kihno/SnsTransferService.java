package com.muzlive.kitpage.kitpage.service.transfer.kihno;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzlive.kitpage.kitpage.config.transfer.domain.MuzDomain;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.VideoGetReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsSubTitle;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoDetailReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoInsertReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsUpsertVideoReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoVttReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.SnsCommonListResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.SnsCommonResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.SnsVideoAndFolderResp;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SnsTransferService {

	@Value("${spring.aws.s3.bucket}")
	private String BUCKET;

	@Value("${spring.page.domain}")
	private String pageDomain;

	private final String VIDEO_INSERT_URL = "/video/v1/insert";

	private final String VIDEO_UPDATE_URL = "/video/v1/update";

	private final String GET_VIDEO_STREAM_URL = "/video/v1/get";

	private final String GET_VIDEO_DETAIL = "/video/v1/detail";

	private final String GET_VIDEO_VTT = "/video/v1/vtt";

	private final String PAGE_VIDEO_DETAIL_URL = "/v1/video/public/detail";

	private final String PAGE_VIDEO_VTT_URL = "/v1/video/public/vtt";

	private WebClient webClient;

	public SnsTransferService(WebClient.Builder builder, MuzDomain muzDomain) {
		this.webClient = builder.baseUrl(muzDomain.getSnsServer()).build();
	}

	public SnsCommonListResp<SnsVideoAndFolderResp> insertVideo(UploadVideoReq uploadVideoReq, Video video) throws Exception {
		SnsSubTitle snsSubTitle = new SnsSubTitle(uploadVideoReq.getSubTitlePath(), uploadVideoReq.getLanguageCode());
		SnsUpsertVideoReq snsUpsertVideoReq = new SnsUpsertVideoReq(uploadVideoReq.getVideoFilePath(), uploadVideoReq.getVideoThumbnailPath(), uploadVideoReq.getTitle(), Integer.valueOf(uploadVideoReq.getDuration()));
		snsUpsertVideoReq.setSubTitleList(List.of(snsSubTitle));
		SnsVideoInsertReq snsVideoInsertReq = new SnsVideoInsertReq("INSERT", video.getPage().getAlbumId(), BUCKET);
		snsVideoInsertReq.setVideoList(List.of(snsUpsertVideoReq));

		return webClient.post()
			.uri(VIDEO_INSERT_URL)
			.accept(MediaType.APPLICATION_JSON)
			.headers(httpHeaders -> httpHeaders.addAll(this.makeDefaultHeader()))
			.body(Mono.just(snsVideoInsertReq), SnsVideoInsertReq.class)
			.retrieve()
			.onStatus(
				HttpStatus::isError, // 상태 코드가 4xx 또는 5xx일 때 처리
				clientResponse -> clientResponse.bodyToMono(String.class)
					.flatMap(errorBody -> {
						log.error("Video Insert Error response from server: status={}, body={}", clientResponse.statusCode(), errorBody);
						return Mono.error(new RuntimeException("Server error: " + errorBody));
					})
			)
			.bodyToMono(new ParameterizedTypeReference<SnsCommonResp<SnsCommonListResp<SnsVideoAndFolderResp>>>() {})
			.timeout(Duration.ofMillis(15000))
			.doOnError(e -> log.error(e.getMessage()))
			.map(SnsCommonResp::getData)
			.block();
	}

	public SnsCommonListResp<SnsVideoAndFolderResp> updateVideo(UploadVideoReq uploadVideoReq, Video video) throws Exception {
		SnsSubTitle snsSubTitle = new SnsSubTitle(uploadVideoReq.getSubTitlePath(), uploadVideoReq.getLanguageCode());
		SnsUpsertVideoReq snsUpsertVideoReq = new SnsUpsertVideoReq(uploadVideoReq.getVideoFilePath(), uploadVideoReq.getVideoThumbnailPath(), uploadVideoReq.getTitle(), Integer.valueOf(uploadVideoReq.getDuration()));
		snsUpsertVideoReq.setSubTitleList(List.of(snsSubTitle));
		snsUpsertVideoReq.setVideoId(uploadVideoReq.getVideoId());
		SnsVideoInsertReq snsVideoInsertReq = new SnsVideoInsertReq("UPDATE", video.getPage().getAlbumId(), BUCKET);
		snsVideoInsertReq.setVideoList(List.of(snsUpsertVideoReq));

		return webClient.post()
			.uri(VIDEO_UPDATE_URL)
			.accept(MediaType.APPLICATION_JSON)
			.headers(httpHeaders -> httpHeaders.addAll(this.makeDefaultHeader()))
			.body(snsVideoInsertReq, SnsVideoInsertReq.class)
			.retrieve()
			.onStatus(
				HttpStatus::isError, // 상태 코드가 4xx 또는 5xx일 때 처리
				clientResponse -> clientResponse.bodyToMono(String.class)
					.flatMap(errorBody -> {
						log.error("Video Update Error response from server: status={}, body={}", clientResponse.statusCode(), errorBody);
						return Mono.error(new RuntimeException("Server error: " + errorBody));
					})
			)
			.bodyToMono(new ParameterizedTypeReference<SnsCommonResp<SnsCommonListResp<SnsVideoAndFolderResp>>>() {})
			.timeout(Duration.ofMillis(15000))
			.doOnError(e -> log.error(e.getMessage()))
			.map(SnsCommonResp::getData)
			.block();
	}

	public String fetchVideoStreamUrl(VideoGetReq videoGetReq) {
		SnsVideoReq snsVideoReq = new SnsVideoReq(
			videoGetReq.getVideoId(), videoGetReq.getAlbumId()
			, pageDomain + PAGE_VIDEO_DETAIL_URL
			, pageDomain + PAGE_VIDEO_VTT_URL);

		// SNS 서버에서 POST + RequestParam 조합으로 받고 있음.
		return webClient.post()
			.uri(uriBuilder -> this.buildUriWithParams(uriBuilder, GET_VIDEO_STREAM_URL, snsVideoReq))
			.accept(MediaType.APPLICATION_JSON)
			.headers(httpHeaders -> httpHeaders.addAll(this.makeBitMovinHeader()))
			//.body(Mono.just(snsVideoReq), SnsVideoReq.class)
			.retrieve()
			.onStatus(
				HttpStatus::isError, // 상태 코드가 4xx 또는 5xx일 때 처리
				clientResponse -> clientResponse.bodyToMono(String.class)
					.flatMap(errorBody -> {
						log.error("Sns Video Get Stream Url Error response from server: status={}, body={}", clientResponse.statusCode(), errorBody);
						return Mono.error(new RuntimeException("Server error: " + errorBody));
					})
			)
			.bodyToMono(new ParameterizedTypeReference<SnsCommonResp<String>>() {})
			.timeout(Duration.ofMillis(15000))
			.doOnError(e -> log.error(e.getMessage()))
			.map(SnsCommonResp::getData)
			.defaultIfEmpty("")
			.block();
	}

	public String fetchVideoDetail(SnsVideoDetailReq snsVideoDetailReq) {
		return webClient.get()
			.uri(uriBuilder -> this.buildUriWithParams(uriBuilder, GET_VIDEO_DETAIL, snsVideoDetailReq))
			.headers(httpHeaders -> httpHeaders.addAll(this.makeBitMovinHeader()))
			.retrieve()
			.onStatus(
				HttpStatus::isError, // 상태 코드가 4xx 또는 5xx일 때 처리
				clientResponse -> clientResponse.bodyToMono(String.class)
					.flatMap(errorBody -> {
						log.error("Get Sns Video Detail Error response from server: status={}, body={}", clientResponse.statusCode(), errorBody);
						return Mono.error(new RuntimeException("Server error: " + errorBody));
					})
			)
			.bodyToMono(new ParameterizedTypeReference<SnsCommonResp<String>>() {})
			.timeout(Duration.ofMillis(15000))
			.doOnError(e -> log.error(e.getMessage()))
			.map(SnsCommonResp::getData)
			.defaultIfEmpty("")
			.block();
	}

	public String fetchVideoVtt(SnsVideoVttReq snsVideoVttReq) {
		return webClient.get()
			.uri(uriBuilder -> this.buildUriWithParams(uriBuilder, GET_VIDEO_VTT, snsVideoVttReq))
			.headers(httpHeaders -> httpHeaders.addAll(this.makeBitMovinHeader()))
			.retrieve()
			.onStatus(
				HttpStatus::isError, // 상태 코드가 4xx 또는 5xx일 때 처리
				clientResponse -> clientResponse.bodyToMono(String.class)
					.flatMap(errorBody -> {
						log.error("Get Sns Video Vtt Error response from server: status={}, body={}", clientResponse.statusCode(), errorBody);
						return Mono.error(new RuntimeException("Server error: " + errorBody));
					})
			)
			.bodyToMono(new ParameterizedTypeReference<SnsCommonResp<String>>() {})
			.timeout(Duration.ofMillis(15000))
			.doOnError(e -> log.error(e.getMessage()))
			.map(SnsCommonResp::getData)
			.defaultIfEmpty("")
			.block();
	}

	private HttpHeaders makeDefaultHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Range","bytes");
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		headers.add("Access-Control-Max-Age", "3600");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Headers", "x-requested-with,Access-Control-Allow-Origin,CopyStreamException,Access-Control-Allow-Methods,Access-Control-Max-Age");
		return headers;
	}

	private HttpHeaders makeBitMovinHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Range","bytes");
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		headers.add("Access-Control-Max-Age", "3600");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Headers", "x-requested-with,Access-Control-Allow-Origin,CopyStreamException,Access-Control-Allow-Methods,Access-Control-Max-Age");
		return headers;
	}

	public ResponseEntity<InputStreamResource> generateM3U8Response(String response) {
		HttpHeaders headers = this.makeBitMovinHeader();
		headers.setContentType(MediaType.parseMediaType("application/x-mpegURL"));

		return Optional.ofNullable(response)
			.filter(str -> !str.isEmpty())
			.map(str -> {
				try {
					byte[] contentBytes = str.getBytes(StandardCharsets.UTF_8);
					ByteArrayInputStream byteStream = new ByteArrayInputStream(contentBytes);
					InputStreamResource resource = new InputStreamResource(byteStream);

					return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.parseMediaType("application/x-mpegURL"))
						.contentLength(contentBytes.length)
						.body(resource);
				} catch (Exception e) {
					return ResponseEntity.internalServerError()
						.headers(headers)
						.body(new InputStreamResource(new ByteArrayInputStream(new byte[0])));
				}
			})
			.orElseGet(() -> ResponseEntity.status(404)
				.headers(headers)
				.contentLength(0)
				.body(new InputStreamResource(new ByteArrayInputStream(new byte[0]))));
	}

	private <T> URI buildUriWithParams(UriBuilder uriBuilder, String path, T request) {
		try {
			if (path == null || path.isEmpty()) {
				throw new IllegalArgumentException("Base URL cannot be null or empty");
			}
			if (request == null) {
				throw new IllegalArgumentException("Request object cannot be null");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> paramMap = objectMapper.convertValue(request, Map.class);

			uriBuilder.path(path);

			paramMap.forEach((key, value) -> {
				if (value != null) {
					uriBuilder.queryParam(key, value.toString());
				}
			});

			return uriBuilder.build(true);
		} catch (Exception e) {
			throw new RuntimeException("Failed to build URI with parameters", e);
		}
	}

}
