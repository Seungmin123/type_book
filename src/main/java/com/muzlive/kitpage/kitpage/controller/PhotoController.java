package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req.ComicBookContentReq;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookDetailResp;
import com.muzlive.kitpage.kitpage.service.page.factory.PageStrategyFactory;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "PhotoBook API")
@RequestMapping("/v1/photo")
@RestController
public class PhotoController {

	private final PageStrategyFactory pageStrategyFactory;

	private PageStrategy<PhotoBookContentResp, PhotoBookDetailResp> strategy;

	@PostConstruct
	public void initStrategy() {
		this.strategy = pageStrategyFactory.getStrategy(PageContentType.PHOTOBOOK);
	}

	@Operation(summary = "PhotoBook 리스트 조회")
	@GetMapping("/list")
	public CommonResp<PhotoBookContentResp> getComicBookListByContentId(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		return new CommonResp<>(strategy.getContentList(comicBookContentReq.getContentId()));
	}

	@Operation(summary = "PhotoBook 컨텐츠 상세 정보 리스트 조회")
	@GetMapping("/detail/list")
	public CommonResp<List<PhotoBookDetailResp>> getComicBookContents(@Valid @ModelAttribute ComicBookContentReq comicBookContentReq) throws Exception {
		return new CommonResp<>(strategy.getContentDetailList(comicBookContentReq.getContentId()));
	}

	@Operation(summary = "PhotoBook 컨텐츠 상세 정보 조회")
	@GetMapping("/detail/{pageUid}")
	public CommonResp<PhotoBookDetailResp> getComicBookContent(@Valid @PathVariable Long pageUid) throws Exception {
		return new CommonResp<>(strategy.getContentDetail(pageUid));
	}

}
