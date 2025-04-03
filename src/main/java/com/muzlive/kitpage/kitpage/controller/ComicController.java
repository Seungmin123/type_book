package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req.ComicBookContentReq;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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

	private final JwtTokenProvider jwtTokenProvider;

	private final PageService pageService;

	private final ComicService comicService;

	@Operation(summary = "ComicBook 리스트 조회")
	@GetMapping("/list")
	public CommonResp<ComicBookContentResp> getComicBookListByContentId(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		return new CommonResp<>(comicService.getComicBookContent(comicBookContentReq.getContentId()));
	}

	@Operation(summary = "ComicBook 컨텐츠 상세 정보 리스트 조회")
	@GetMapping("/detail/list")
	public CommonResp<List<ComicBookDetailResp>> getComicBookContents(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		return new CommonResp<>(comicService.getRelatedComicDetailBookList(comicBookContentReq.getContentId()));
	}

	@Operation(summary = "ComicBook 컨텐츠 상세 정보 조회")
	@GetMapping("/detail/{pageUid}")
	public CommonResp<ComicBookDetailResp> getComicBookContent(@Valid @PathVariable Long pageUid) throws Exception {
		Page page = pageService.findPageById(pageUid);
		return new CommonResp<>(comicService.getComicBookDetail(page));
	}

}
