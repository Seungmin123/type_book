package com.muzlive.kitpage.kitpage.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreateContentReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreateKitReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreatePageReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadPhotoBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.service.google.YoutubeService;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.PhotoService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.SnsTransferService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.VideoEncodingTransferService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoInsertReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.SnsCommonListResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.SnsVideoAndFolderResp;
import com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp.KittorTokenResp;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@RestController
public class AdminController {

	private final PageService pageService;

	private final ComicService comicService;

	private final PhotoService photoService;

	private final SnsTransferService snsTransferService;

	private final YoutubeService youtubeService;

	private final CommonUtils commonUtils;

	@PostMapping("/page")
	CommonResp<String> createPage(@Valid @ModelAttribute CreatePageReq createPageReq) throws Exception {
		return new CommonResp<>(pageService.createPage(createPageReq).getContentId());
	}

	@PostMapping("/content")
	CommonResp<Void> createContent(@Valid @ModelAttribute CreateContentReq createContentReq) throws Exception {
		pageService.createContent(createContentReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic")
	CommonResp<Long> uploadComicBook(@Valid @ModelAttribute UploadComicBookReq uploadComicBookReq) throws Exception {
		return new CommonResp<>(comicService.insertComicBook(
			pageService.findPageById(uploadComicBookReq.getPageUid()).getContentId(),
			uploadComicBookReq
		).getComicBookUid());
	}

	@PostMapping("/comic/detail")
	CommonResp<Void> uploadComicDetail(@Valid @ModelAttribute UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		comicService.insertComicBookDetail(uploadComicBookDetailReq);
		return new CommonResp<>();
	}

	@PostMapping("/photo")
	CommonResp<Long> uploadPhotoBook(@Valid @ModelAttribute UploadPhotoBookReq uploadPhotoBookReq) throws Exception {
		return new CommonResp<>(
			photoService.insertPhotoBook(
				pageService.findPageById(uploadPhotoBookReq.getPageUid()).getContentId(),
				uploadPhotoBookReq
			).getPhotoBookUid()
		);
	}

	@PostMapping("/photo/detail")
	CommonResp<Void> uploadPhotoDetail(@Valid @ModelAttribute UploadPhotoBookDetailReq uploadPhotoBookDetailReq) throws Exception {
		photoService.insertPhotoBookDetail(uploadPhotoBookDetailReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic/music")
	CommonResp<Void> uploadMusic(@Valid @ModelAttribute UploadMusicReq uploadMusicReq) throws Exception {
		pageService.insertMusic(uploadMusicReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic/video")
	CommonResp<SnsVideoInsertReq> uploadVideo(@Valid @ModelAttribute UploadVideoReq uploadVideoReq) throws Exception {
		if(uploadVideoReq.getVideoCode().equals(VideoCode.BITMOVIN)) {
			Video video = pageService.insertVideo(uploadVideoReq, VideoCode.BITMOVIN);

			SnsCommonListResp<SnsVideoAndFolderResp> videoEncodingInfo = snsTransferService.insertVideo(uploadVideoReq, video);
			Long coverImageUid = pageService.uploadThumbnail(uploadVideoReq.getContentId(), uploadVideoReq.getVideoThumbnailPath());
			if(!CollectionUtils.isEmpty(videoEncodingInfo.getList())) {
				videoEncodingInfo.getList().forEach(videoEncoding -> {
					video.setVideoId(videoEncoding.getVideoId());
					video.setCoverImageUid(coverImageUid);
					pageService.upsertVideo(video);
				});
			}

		} else if (uploadVideoReq.getVideoCode().equals(VideoCode.YOUTUBE)) {
			Video video = pageService.insertVideo(uploadVideoReq, VideoCode.YOUTUBE);

			List<com.google.api.services.youtube.model.Video> youtubeResp = youtubeService.getVideoDetail(video.getStreamUrl());
			if(CollectionUtils.isEmpty(youtubeResp))
				throw new CommonException(ExceptionCode.YOUTUBE_UPLOAD_ERROR);

			String duration = commonUtils.convertDurationToString(Duration.parse(youtubeResp.get(0).getContentDetails().getDuration()));
			String title = youtubeResp.get(0).getSnippet().getTitle();
			String url = youtubeService.getYoutubeThumbnailUrl(youtubeResp);

			Long imageUid = pageService.uploadYoutubeThumbnail(video.getContentId(), url);

			video.setCoverImageUid(imageUid);
			video.setTitle(title);
			video.setDuration(duration);
			pageService.upsertVideo(video);
		}

		return new CommonResp<>();
	}

	@PostMapping("/porting")
	CommonResp<Void> createKit(@RequestBody JsonNode createKitReqs) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		List<CreateKitReq> kits = new ArrayList<>();

		if(createKitReqs.isArray()) {
			kits = mapper.convertValue(createKitReqs, new TypeReference<List<CreateKitReq>>() {
			});
		} else if(createKitReqs.isObject()) {
			CreateKitReq singleKit = mapper.convertValue(createKitReqs, CreateKitReq.class);
			kits.add(singleKit);
		} else {
			throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);
		}

		pageService.createKit(kits);

		return new CommonResp<>();
	}

	@PutMapping("/porting")
	CommonResp<Void> updateKit(@RequestBody JsonNode createKitReqs) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		List<CreateKitReq> kits = new ArrayList<>();

		if(createKitReqs.isArray()) {
			kits = mapper.convertValue(createKitReqs, new TypeReference<List<CreateKitReq>>() {
			});
		} else if(createKitReqs.isObject()) {
			CreateKitReq singleKit = mapper.convertValue(createKitReqs, CreateKitReq.class);
			kits.add(singleKit);
		} else {
			throw new CommonException(ExceptionCode.INVALID_REQUEST_PRAMETER);
		}

		pageService.updateKit(kits);

		return new CommonResp<>();
	}

	@DeleteMapping("/porting")
	CommonResp<Void> deleteKit(
		@RequestParam("appId") String appId,
		@RequestParam("serialNumber") String serialNumber
	) throws Exception {
		List<CreateKitReq> kits = List.of(new CreateKitReq(appId, serialNumber));
		pageService.deleteKit(kits);
		return new CommonResp<>();
	}

	@Hidden
	@PostMapping("/image")
	CommonResp<Long> uploadImage(CreateContentReq createContentReq) throws Exception {
		return new CommonResp<>(
			pageService.saveImage(
				createContentReq.getContentId()
				, FilenameUtils.getExtension(createContentReq.getImage().getOriginalFilename())
				, createContentReq.getImage().getBytes()
			)
		);
	}
}
