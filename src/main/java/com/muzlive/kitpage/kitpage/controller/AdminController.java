package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreateContentReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreatePageReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.service.google.YoutubeService;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.MuzTransferService;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@RestController
public class AdminController {

	private final PageService pageService;

	private final ComicService comicService;

	private final MuzTransferService muzTransferService;

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

	@PostMapping("/comic/music")
	CommonResp<Void> uploadMusic(@Valid @ModelAttribute UploadMusicReq uploadMusicReq) throws Exception {
		pageService.insertMusic(uploadMusicReq);
		return new CommonResp<>();
	}

	@Transactional
	@PostMapping("/comic/video")
	CommonResp<Void> uploadVideo(@Valid @ModelAttribute UploadVideoReq uploadVideoReq) throws Exception {
		Video video = pageService.insertVideo(uploadVideoReq);

		if(video.getVideoCode().equals(VideoCode.S3)) {
			Map<String, Object> videoEncodingInfo = muzTransferService.encodingVideo(video.getStreamUrl());

			if (videoEncodingInfo.containsKey("video_id")) {
				video.setVideoId(String.valueOf(videoEncodingInfo.get("video_id")));
				pageService.upsertVideo(video);
			}
		} else if (video.getVideoCode().equals(VideoCode.YOUTUBE)) {
			List<com.google.api.services.youtube.model.Video> youtubeResp = youtubeService.getVideoDetail(video.getStreamUrl());
			if(CollectionUtils.isEmpty(youtubeResp))
				throw new CommonException(ExceptionCode.YOUTUBE_UPLOAD_ERROR);

			String duration = commonUtils.convertDurationToString(Duration.parse(youtubeResp.get(0).getContentDetails().getDuration()));
			String title = youtubeResp.get(0).getSnippet().getTitle();
			String url = null;
			if(youtubeResp.get(0).getSnippet().getThumbnails().getStandard() != null) {
				url = youtubeResp.get(0).getSnippet().getThumbnails().getStandard().getUrl();
			} else if(youtubeResp.get(0).getSnippet().getThumbnails().getHigh() != null) {
				url = youtubeResp.get(0).getSnippet().getThumbnails().getHigh().getUrl();
			} else if(youtubeResp.get(0).getSnippet().getThumbnails().getMedium() != null) {
				url = youtubeResp.get(0).getSnippet().getThumbnails().getMedium().getUrl();
			} else {
				url = youtubeResp.get(0).getSnippet().getThumbnails().getDefault().getUrl();
			}

			Long imageUid = pageService.uploadYoutubeThumbnail(video.getContentId(), url);

			video.setCoverImageUid(imageUid);
			video.setTitle(title);
			video.setDuration(duration);
			pageService.upsertVideo(video);
		}

		return new CommonResp<>();
	}
}
