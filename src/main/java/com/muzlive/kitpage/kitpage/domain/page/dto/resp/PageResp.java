package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResp {

	private Long coverImageUid;

	private Long pageUid;

	private PageContentType contentType;

	private String title;

	private String writer;

	private String illustrator;

	public PageResp(Page page) {
		this.coverImageUid = page.getCoverImageUid();
		this.pageUid = page.getPageUid();
		this.contentType = page.getContentType();
		this.title = page.getTitle();
	}


}
