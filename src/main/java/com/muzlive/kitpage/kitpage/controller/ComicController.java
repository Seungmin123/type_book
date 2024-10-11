package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req.ComicBookContentReq;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookRelatedResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "ComicBook API")
@RequestMapping("/v1/comic")
@RestController
public class ComicController {

	private final PageService pageService;

	private final ComicService comicService;

	// TODO !!! 북마크 로컬, 이어보기 로
	// TODO 비디오 비트무빈 유튜브
	// m368
	// TODO Kihno API 확인
	// TODO 설정 마이페이지 -> 통합로그인...
	// TODO 책 방향 왼쪽 오른쪽 주기 알려주기

	@Operation(summary = "연관된 Comic Kit 리스트 조회")
	@GetMapping
	public CommonResp<ComicBookRelatedResp> getRelatedComicBookList(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		return new CommonResp<>(comicService.getRelatedComicBookList(comicBookContentReq.getPageUid(), comicBookContentReq.getDeviceId()));
	}


	@Operation(summary = "ComicBook 컨텐츠 상세 정보 조회 - 비디오 추가 안됨")
	@GetMapping("/detail/{pageUid}")
	public CommonResp<ComicBookDetailResp> getComicBookContent(@Valid @PathVariable Long pageUid) throws Exception {
		Page page = pageService.findPageById(pageUid);
		ComicBookDetailResp comicBookDetailResp = new ComicBookDetailResp(page);
		comicBookDetailResp.setDetails(comicService.getEpisodeResps(page));

		// TODO 비디오 추가

		return new CommonResp<>(comicBookDetailResp);
	}

	@Operation(summary = "ComicBook 컨텐츠 상세 정보 리스트 조회 - 비디오 추가 안됨")
	@GetMapping("/detail/list")
	public CommonResp<List<ComicBookDetailResp>> getComicBookContents(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		// TODO 비디오 추가
		return new CommonResp<>(comicService.getRelatedComicDetailBookList(comicBookContentReq.getPageUid(), comicBookContentReq.getDeviceId()));
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
