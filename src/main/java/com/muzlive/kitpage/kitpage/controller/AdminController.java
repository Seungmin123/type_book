package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.CreatePageReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookDetailReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadComicBookReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadMusicReq;
import com.muzlive.kitpage.kitpage.domain.page.dto.req.UploadVideoReq;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@RestController
public class AdminController {

	private final PageService pageService;

	@PostMapping("/page")
	CommonResp<Void> createPage(@Valid @ModelAttribute CreatePageReq createPageReq) throws Exception {
		pageService.createPage(createPageReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic")
	CommonResp<Void> uploadComicBook(@Valid @ModelAttribute UploadComicBookReq uploadComicBookReq) throws Exception {
		pageService.insertComicBook(uploadComicBookReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic/detail")
	CommonResp<Void> uploadComicDetail(@Valid @ModelAttribute UploadComicBookDetailReq uploadComicBookDetailReq) throws Exception {
		pageService.insertComicBookDetail(uploadComicBookDetailReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic/music")
	CommonResp<Void> uploadMusic(@Valid @ModelAttribute UploadMusicReq uploadMusicReq) throws Exception {
		pageService.insertMusic(uploadMusicReq);
		return new CommonResp<>();
	}

	@PostMapping("/comic/video")
	CommonResp<Void> uploadVideo(@Valid @ModelAttribute UploadVideoReq uploadVideoReq) throws Exception {
		pageService.insertVideo(uploadVideoReq);
		return new CommonResp<>();
	}
}
