package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.encryptor.AesSecurityProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.PageResp;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.service.aws.CloudFrontService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@Hidden
	@Operation(summary = "앨범 목록 API", description = "Device Id 별 앨범 리스트")
	@GetMapping("/list/{deviceId}")
	public CommonResp<List<PageResp>> getInstallList(@PathVariable String deviceId) throws Exception {
		List<Page> pages = pageService.findByDeviceId(deviceId);

		List<PageResp> pageResps = new ArrayList<>();
		for(Page page : pages) {
			if(CollectionUtils.isEmpty(page.getComicBooks()))
				continue;

			PageResp pageResp = new PageResp(page);
			pageResp.setWriter(page.getComicBooks().get(0).getWriter());
			pageResp.setIllustrator(page.getComicBooks().get(0).getIllustrator());

			pageResps.add(pageResp);
		}

		return new CommonResp<>(pageResps);
	}

	@Operation(summary = "이미지 다운로드 API - Decrypted", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long imageUid) throws Exception {
		Image image = pageService.findImageById(imageUid);
		byte[] decryptedImage = aesSecurityProvider.decrypt(cloudFrontService.getCFImageByKey(image.getImagePath()));
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getSaveFileName() + "\"")
			.body(new InputStreamResource(new ByteArrayInputStream(decryptedImage)));
	}

	@Operation(summary = "이미지 다운로드 API - Encrypted", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/encrypt/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImageEncrypted(@PathVariable Long imageUid) throws Exception {
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
