package com.muzlive.kitpage.kitpage.service.page.strategy;

import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;

public interface PageStrategy {

	// TODO 컨텐츠 추가될 경우 공통적으로 필요한 메소드 추가

	PageContentType getSupportedType();

	long getTotalSize(Long pageUid);

}
