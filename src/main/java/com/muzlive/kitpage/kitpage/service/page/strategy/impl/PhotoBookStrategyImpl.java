package com.muzlive.kitpage.kitpage.service.page.strategy.impl;

import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookDetailResp;
import com.muzlive.kitpage.kitpage.service.page.PageService;
import com.muzlive.kitpage.kitpage.service.page.PhotoService;
import com.muzlive.kitpage.kitpage.service.page.strategy.PageStrategy;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PhotoBookStrategyImpl implements PageStrategy<PhotoBookContentResp, PhotoBookDetailResp> {

	private final PageService pageService;

	private final PhotoService photoService;

	@Override
	public PhotoBookContentResp getContentList(String contentId) {
		return photoService.getPhotoBookContent(contentId);
	}

	@Override
	public List<PhotoBookDetailResp> getContentDetailList(String contentId, ClientPlatformType clientPlatformType) {
		return photoService.getRelatedPhotoDetailBookList(contentId, clientPlatformType);
	}

	@Override
	public PhotoBookDetailResp getContentDetail(long pageUid, ClientPlatformType clientPlatformType) {
		return photoService.getPhotoBookDetail(pageService.findPageById(pageUid), clientPlatformType);
	}

	@Override
	public PageContentType getSupportedType() {
		return PageContentType.PHOTOBOOK;
	}

	@Override
	public long getTotalSize(Long pageUid, ClientPlatformType clientPlatformType) {
		return photoService.getImageSizeByPageUid(pageUid, clientPlatformType);
	}
}
