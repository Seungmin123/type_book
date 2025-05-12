package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.aspect.ClientPlatform;
import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.config.aspect.CurrentToken;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.ContentReq;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.ContentListReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.ContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.service.aws.CloudFrontService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.factory.PageStrategyFactory;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Page API")
@RequestMapping("/v1/page")
@RestController
public class PageController {

	private final PageService pageService;

	private final CloudFrontService cloudFrontService;

	private final AesSecurityProvider aesSecurityProvider;

	private final JwtTokenProvider jwtTokenProvider;

	private final CommonUtils commonUtils;

	private final PageStrategyFactory pageStrategyFactory;

	private PageStrategy<? extends CommonContentResp, ? extends CommonContentDetailResp> strategy;

	@Operation(summary = "앨범 목록 API", description = "Device Id 별 앨범 리스트<br>"
		+ "listSize - optional - default 20<br>"
		+ "contentUid - optional - 스크롤 대비용<br>"
		+ "descending - optional - default 'true' - 정렬 순서 변경<br>"
		+ "searchValue - optional - 검색 대비용<br>")
	@GetMapping("/list")
	public CommonResp<List<ContentResp>> getInstallList(ContentListReq contentListReq, @CurrentToken String jwt) throws Exception {
		contentListReq.setDeviceId(jwtTokenProvider.getDeviceIdByToken(jwt));

		List<ContentResp> contentResps = pageService.findContentList(contentListReq).stream()
			.map(ContentResp::new)
			.collect(Collectors.toList());

		return new CommonResp<>(contentResps);
	}

	@Operation(summary = "컨텐츠 리스트 조회")
	@GetMapping("/content/list")
	public CommonResp<? extends CommonContentResp> getComicBookListByContentId(@Valid @ModelAttribute ContentReq contentReq) throws Exception {
		strategy = pageStrategyFactory.getStrategy(pageService.findContentByContentId(contentReq.getContentId()).getContentType());
		return new CommonResp<>(strategy.getContentList(contentReq.getContentId()));
	}

	@Operation(summary = "컨텐츠 상세 정보 리스트 조회")
	@GetMapping("/content/detail/list")
	public CommonResp<List<? extends CommonContentDetailResp>> getComicBookContents(
		@Valid @ModelAttribute ContentReq contentReq,

		@Parameter(
			name = "X-Client-Platform",
			description = "클라이언트 플랫폼 정보",
			in = ParameterIn.HEADER
		)
		@ClientPlatform ClientPlatformType clientPlatformType) throws Exception {
		strategy = pageStrategyFactory.getStrategy(pageService.findContentByContentId(contentReq.getContentId()).getContentType());
		return new CommonResp<>(strategy.getContentDetailList(contentReq.getContentId(), clientPlatformType));
	}

	@Operation(summary = "컨텐츠 상세 정보 조회")
	@GetMapping("/content/detail/{pageUid}")
	public CommonResp<? extends CommonContentDetailResp> getComicBookContent(
		@Valid @PathVariable Long pageUid,

		@Parameter(
			name = "X-Client-Platform",
			description = "클라이언트 플랫폼 정보",
			in = ParameterIn.HEADER
		)
		@ClientPlatform ClientPlatformType clientPlatformType) throws Exception {
		strategy = pageStrategyFactory.getStrategy(pageService.findPageById(pageUid).getContent().getContentType());
		return new CommonResp<>(strategy.getContentDetail(pageUid, clientPlatformType));
	}

	@Deprecated
	@Operation(summary = "이미지 다운로드 API - Encrypted", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/encrypt/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImageEncrypted(@PathVariable Long imageUid, @CurrentToken String jwt) throws Exception {
		Image image = pageService.findImageById(imageUid);
		byte[] encryptedImage = cloudFrontService.getCFImageByKey(image.getImagePath());
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getSaveFileName() + "\"")
			.body(new InputStreamResource(new ByteArrayInputStream(encryptedImage)));
	}

	@Operation(summary = "파일 다운로드 URL 조회 API - Encrypted", description = "filePath(Base64) 를 통해 다운로드 Signed Url 조회")
	@GetMapping("/download/{filePath}")
	public CommonResp<String> getSignedUrl(@PathVariable String filePath) throws Exception {
		return new CommonResp<>(cloudFrontService.getSignedUrl(commonUtils.base64Decode(filePath)));
	}

	// TODO ImageUid 제거
	@Operation(summary = "이미지 View API", description = "Long 타입 ImageUid / String 타입 saveFilePath(base64) 가능<br>"
		+ "ImageUid 제거 예정")
	@GetMapping("/view/{imageUidOrSaveFilePath}")
	public ResponseEntity<byte[]> viewImage(@PathVariable String imageUidOrSaveFilePath) throws Exception {
		String filePath;
		if (imageUidOrSaveFilePath.matches("\\d+")) {
			Long imageUid = Long.parseLong(imageUidOrSaveFilePath);
			Image image = pageService.findImageById(imageUid);
			filePath = image.getImagePath();
		} else {
			filePath = commonUtils.base64Decode(imageUidOrSaveFilePath);
		}

		byte[] decryptedImage = aesSecurityProvider.decrypt(cloudFrontService.getCFImageByKey(filePath));
		try {
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(filePath)))
				.body(decryptedImage);
		} catch (InvalidMediaTypeException e) {
			log.error(e.getMessage());
			return ResponseEntity.badRequest()
				.body(null);
		}
	}
}
