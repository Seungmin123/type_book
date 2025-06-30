package com.muzlive.kitpage.kitpage.service.page.strategy;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import com.muzlive.kitpage.kitpage.utils.enums.ClientPlatformType;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.List;

public interface PageStrategy<T extends CommonContentResp, D extends CommonContentDetailResp> {

	// 컨텐츠 추가될 경우 공통적으로 필요한 메소드 추가

	// 템플릿 메소드 패턴 고민 필요.

	default T getContentList(String contentId) {
		throw new UnsupportedOperationException("not implemented");
	};

	default List<D> getContentDetailList(String contentId, ClientPlatformType clientPlatformType) {
		throw new UnsupportedOperationException("not implemented");
	};

	default D getContentDetail(long pageUid, ClientPlatformType clientPlatformType) {
		throw new UnsupportedOperationException("not implemented");
	};

	PageContentType getSupportedType();

	default long getTotalSize(Long pageUid, ClientPlatformType clientPlatformType) {
		return 0L;
	};

}
