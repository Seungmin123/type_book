package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookEpisodeResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.PageResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

	// TODO !!! 북마크 로컬, 이어보기 로
	// TODO 비디오 비트무빈 유튜브
	// m368
	// TODO Kihno API 확인
	// TODO 설정 마이페이지 -> 통합로그인...
	// TODO 책 방향 왼쪽 오른쪽 주기 알려주기

	@Hidden
	@GetMapping("/download")
	public CommonResp<Void> downloadComicBook() throws Exception {

		return new CommonResp<>();
	}

	@GetMapping("/detail/{pageUid}")
	public CommonResp<ComicBookDetailResp> getComicBookInfo(@PathVariable Long pageUid) throws Exception {
		Page page = pageService.findPageById(pageUid);
		if(CollectionUtils.isEmpty(page.getComicBooks()))
			throw new CommonException(ExceptionCode.CANNOT_FIND_ITEM_THAT_MATCH_THE_PARAM);

		ComicBookDetailResp comicBookDetailResp = new ComicBookDetailResp(page);
		comicBookDetailResp.setWriter(page.getComicBooks().get(0).getWriter());
		comicBookDetailResp.setIllustrator(page.getComicBooks().get(0).getIllustrator());

		List<ComicBookEpisodeResp> comicBookEpisodeResps = new ArrayList<>();
		for(ComicBook comicBook : page.getComicBooks()) {
			comicBookEpisodeResps.add(new ComicBookEpisodeResp(comicBook, ApplicationConstants.COMIC_BOOK_UNIT_1));
		}
		comicBookDetailResp.setDetails(comicBookEpisodeResps);

		// TODO add video


		return new CommonResp<>(comicBookDetailResp);
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
