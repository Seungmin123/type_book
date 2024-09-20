package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/comic")
@RestController
public class ComicController {

	private final ComicService comicService;

	// get Install ComicBook List
	@GetMapping("/list/{deviceId}")
	public CommonResp<List<ComicBookResp>> getInstallList(@PathVariable String deviceId) throws Exception {
		return new CommonResp<>(ComicBookResp.of(comicService.getComicBooksByDeviceId(deviceId)));
	}

	// TODO download comicBook
	public CommonResp<Void> downloadComicBook() throws Exception {
		return new CommonResp<>();
	}

	// TODO get ComicBook Info By contentId
	public CommonResp<Void> getComicBookInfo() throws Exception {
		return new CommonResp<>();
	}

	// TODO get ComicBook Detail list By contentId
	public CommonResp<Void> getComicBookDetaiList() throws Exception {
		return new CommonResp<>();
	}

	// TODO get ComicBook Detail By comicBookDetailUid
	public CommonResp<Void> getComicBookDetailInfo() throws Exception {
		return new CommonResp<>();
	}

	// TODO get BookMark list
	public CommonResp<Void> getBookMarkList() throws Exception {
		return new CommonResp<>();
	}

	// TODO Insert BookMark
	public CommonResp<Void> insertBookMark() throws Exception {
		return new CommonResp<>();
	}

	// TODO Delete BookMark
	public CommonResp<Void> deleteBookMark() throws Exception {
		return new CommonResp<>();
	}

	// TODO Image View APi -> ? -> Online -> image return

	// TODO get Last View Location
	public CommonResp<Void> getComicBookLatestView() throws Exception {
		return new CommonResp<>();
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
