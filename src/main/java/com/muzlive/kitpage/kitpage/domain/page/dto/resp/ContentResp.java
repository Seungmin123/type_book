package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentResp {

	private Long coverImageUid;

	private String contentId;

	private PageContentType contentType;

	private String title;

	private String writer;

	public ContentResp(Content content) {
		this.coverImageUid = content.getCoverImageUid();
		this.contentId = content.getContentId();
		this.contentType = content.getContentType();
		this.title = content.getTitle();
	}


}
