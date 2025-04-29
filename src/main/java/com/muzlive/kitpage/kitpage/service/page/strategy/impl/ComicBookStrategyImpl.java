package com.muzlive.kitpage.kitpage.service.page.strategy.impl;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ComicBookStrategyImpl implements PageStrategy<ComicBookContentResp, ComicBookDetailResp> {

	private final PageService pageService;

	private final ComicService comicService;

	@Override
	public ComicBookContentResp getContentList(String contentId) {
		return comicService.getComicBookContent(contentId);
	}

	@Override
	public List<ComicBookDetailResp> getContentDetailList(String contentId, ClientPlatformType clientPlatformType) {
		return comicService.getRelatedComicDetailBookList(contentId);
	}

	@Override
	public ComicBookDetailResp getContentDetail(long pageUid, ClientPlatformType clientPlatformType) {
		return comicService.getComicBookDetail(pageService.findPageById(pageUid));
	}

	@Override
	public PageContentType getSupportedType() {
		return PageContentType.COMICBOOK;
	}

	@Override
	public long getTotalSize(Long pageUid, ClientPlatformType clientPlatformType) {
		return comicService.getImageSizeByPageUid(pageUid);
	}
}
