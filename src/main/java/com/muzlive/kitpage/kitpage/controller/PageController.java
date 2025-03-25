package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.ContentListReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.ContentResp;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.service.aws.CloudFrontService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Operation(summary = "앨범 목록 API", description = "Device Id 별 앨범 리스트<br>"
		+ "listSize - optional - default 20<br>"
		+ "contentUid - optional - 스크롤 대비용<br>"
		+ "descending - optional - default 'true' - 정렬 순서 변경<br>"
		+ "searchValue - optional - 검색 대비용<br>")
	@GetMapping("/list")
	public CommonResp<List<ContentResp>> getInstallList(ContentListReq contentListReq, HttpServletRequest httpServletRequest) throws Exception {
		String token = jwtTokenProvider.resolveToken(httpServletRequest);
		contentListReq.setDeviceId(jwtTokenProvider.getDeviceIdByToken(token));

		List<ContentResp> contentResps = pageService.findContentList(contentListReq).stream()
			.map(ContentResp::new)
			.collect(Collectors.toList());

		return new CommonResp<>(contentResps);
	}

	@Operation(summary = "이미지 다운로드 API - Decrypted", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long imageUid, HttpServletRequest httpServletRequest) throws Exception {

		// ImageUid, SerialNumber 유효성 검사
		if(!pageService.existsByImageUidAndSerialNumber(imageUid, jwtTokenProvider.getSerialNumberByToken(jwtTokenProvider.resolveToken(httpServletRequest))) // 토큰의 seiralNumber에 해당하는 키트와 이미지가 매칭되는 지 확인
			&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().noneMatch(v -> v.getAuthority().equals(UserRole.ENGINEER.getKey()))) // 관리자
			throw new CommonException(ExceptionCode.NON_DOWNLOADABLE_TOKEN);


		Image image = pageService.findImageById(imageUid);
		byte[] decryptedImage = aesSecurityProvider.decrypt(cloudFrontService.getCFImageByKey(image.getImagePath()));
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getSaveFileName() + "\"")
			.body(new InputStreamResource(new ByteArrayInputStream(decryptedImage)));
	}

	@Operation(summary = "이미지 다운로드 API - Encrypted", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/encrypt/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImageEncrypted(@PathVariable Long imageUid, HttpServletRequest httpServletRequest) throws Exception {
		// ImageUid, SerialNumber 유효성 검사
		if(!pageService.existsByImageUidAndSerialNumber(imageUid, jwtTokenProvider.getSerialNumberByToken(jwtTokenProvider.resolveToken(httpServletRequest))) // 토큰의 seiralNumber에 해당하는 키트와 이미지가 매칭되는 지 확인
			&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().noneMatch(v -> v.getAuthority().equals(UserRole.ENGINEER.getKey()))) // 관리자
			throw new CommonException(ExceptionCode.NON_DOWNLOADABLE_TOKEN);

		Image image = pageService.findImageById(imageUid);
		byte[] encryptedImage = cloudFrontService.getCFImageByKey(image.getImagePath());
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getSaveFileName() + "\"")
			.body(new InputStreamResource(new ByteArrayInputStream(encryptedImage)));
	}

	@Operation(summary = "이미지 View API")
	@GetMapping("/view/{imageUid}")
	public ResponseEntity<byte[]> viewImage(@PathVariable Long imageUid) throws Exception {
		Image image = pageService.findImageById(imageUid);
		byte[] decryptedImage = aesSecurityProvider.decrypt(cloudFrontService.getCFImageByKey(image.getImagePath()));
		try {
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(image.getSaveFileName())))
				.body(decryptedImage);
		} catch (InvalidMediaTypeException e) {
			log.error(e.getMessage());
			return ResponseEntity.badRequest()
				.body(null);
		}
	}
}
