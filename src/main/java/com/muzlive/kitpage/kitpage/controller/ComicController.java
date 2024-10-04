package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req.ComicBookDownloadReq;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.PageResp;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import com.muzlive.kitpage.kitpage.service.aws.S3Service;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "ComicBook API")
@RequestMapping("/v1/comic")
@RestController
public class ComicController {

	private final PageService pageService;

	private final ComicService comicService;

	private final S3Service s3Service;

	// TODO !!! 북마크 로컬, 이어보기 로
	// TODO 비디오 비트무빈 유튜브
	// m368
	// TODO Kihno API 확인
	// TODO 설정 마이페이지 -> 통합로그인...
	// TODO 책 방향 왼쪽 오른쪽 주기 알려주기

//	@Operation(summary = "컨텐츠 다운로드 API", description = "pageUid 를 통해 ZIP 파일 다운로드")
//	@GetMapping("/download/{pageUid}")
//	public ResponseEntity<InputStreamResource> downloadComicBook(@PathVariable Long pageUid) throws Exception {
//		List<Image> images = pageService.findByPageUid(pageUid);
//
//		if(CollectionUtils.isEmpty(images)) {
//			return ResponseEntity.status(400)
//				.body(null);
//		}
//
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
//
//		for(Image image : images) {
//			InputStream resource = s3Service.downloadFile(image.getImagePath());
//
//			zipOutputStream.putNextEntry(new ZipEntry(image.getSaveFileName()));
//			byte[] buffer = new byte[1024];
//			int length;
//			while ((length = resource.read(buffer)) > 0) {
//				zipOutputStream.write(buffer, 0, length);
//			}
//			zipOutputStream.closeEntry();
//		}
//
//		zipOutputStream.finish();
//		zipOutputStream.close();
//
//		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//
//		return ResponseEntity.ok()
//			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pageUid + ".zip\"")
//			.body(resource);
//	}

	@Operation(summary = "이미지 다운로드 API", description = "imageUid 를 통해 Image 파일 다운로드")
	@GetMapping("/download/image/{imageUid}")
	public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long imageUid) throws Exception {
		Image image = pageService.findImageById(imageUid);
		InputStream resource = s3Service.downloadFile(image.getImagePath());

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getSaveFileName() + "\"")
			.body(new InputStreamResource(resource));
	}

	@Operation(summary = "ComicBook 정보 조회 API")
	@GetMapping("/{pageUid}")
	public CommonResp<ComicBookResp> getComicBookInfo(@PathVariable Long pageUid) throws Exception {
		Page page = pageService.findPageById(pageUid);
		if(CollectionUtils.isEmpty(page.getComicBooks()))
			throw new CommonException(ExceptionCode.CANNOT_FIND_ITEM_THAT_MATCH_THE_PARAM);

		ComicBookResp comicBookResp = new ComicBookResp(page);
		comicBookResp.setWriter(page.getComicBooks().get(0).getWriter());
		comicBookResp.setIllustrator(page.getComicBooks().get(0).getIllustrator());

		List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();
		for(ComicBook comicBook : page.getComicBooks()) {
			comicBookEpisodeResps.add(new ComicBookEpisodeResp(comicBook, ApplicationConstants.COMIC_BOOK_UNIT_1));
		}
		comicBookResp.setDetails(comicBookEpisodeResps);

		// TODO add video

		return new CommonResp<>(comicBookResp);
	}

	// TODO get Music Info
	public CommonResp<Void> getMusicInfo() throws Exception {
		return new CommonResp<>();
	}

	// TODO get Video Info
	public CommonResp<Void> getVideoInfo() throws Exception {
		return new CommonResp<>();
	}
}
