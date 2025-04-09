package com.muzlive.kitpage.kitpage.service.page.strategy.impl;

import com.muzlive.kitpage.kitpage.service.page.ComicService;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ComicBookStrategyImpl implements PageStrategy {

	private final ComicService comicService;

	@Override
	public PageContentType getSupportedType() {
		return PageContentType.COMICBOOK;
	}

	@Override
	public long getTotalSize(Long pageUid) {
		return comicService.getImageSizeByPageUid(pageUid);
	}
}
