package com.muzlive.kitpage.kitpage.domain.user.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTagResp {

	private String token;

	private Long pageUid;

	private String contentId;

	private String albumId;

	private Long coverImageUid;

	private String title;

	private String writer;

	private Region region;

	private Long totalSize;

	private PageContentType contentType;

	public CheckTagResp(Page page, String token) {
		this.token = token;
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.albumId = page.getAlbumId();
		this.coverImageUid = page.getCoverImageUid();
		this.title = page.getTitle();
		this.contentType = page.getContent().getContentType();
		this.writer = Objects.nonNull(page.getContent()) ? page.getContent().getWriter() : "";
		this.region = Objects.nonNull(page.getContent()) ? page.getContent().getRegion() : null;
	}

}
