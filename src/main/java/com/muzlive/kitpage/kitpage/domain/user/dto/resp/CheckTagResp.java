package com.muzlive.kitpage.kitpage.domain.user.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTagResp {

	private String token;

	private Long pageUid;

	private String contentId;

	private Long coverImageUid;

	private String title;

	private String writer;

	public CheckTagResp(Page page, String token) {
		this.token = token;
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.coverImageUid = page.getCoverImageUid();
		this.title = page.getSubTitle();
		this.writer = (Objects.nonNull(page.getComicBooks().get(0))) ? page.getComicBooks().get(0).getWriter() : "";
	}

}
