package com.muzlive.kitpage.kitpage.service.page.strategy;

import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.dto.resp.CommonContentResp;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import java.util.List;

public interface PageStrategy<T extends CommonContentResp, D extends CommonContentDetailResp> {

	// TODO 컨텐츠 추가될 경우 공통적으로 필요한 메소드 추가

	T getContentList(String contentId);

	List<D> getContentDetailList(String contentId);

	D getContentDetail(long pageUid);

	PageContentType getSupportedType();

	long getTotalSize(Long pageUid);

}
