package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonContentDetailResp {

	private Long pageUid;

	private String contentId;

	private String albumId;

	private String infoText;

	public CommonContentDetailResp(Page page) {
		this.pageUid = page.getPageUid();
		this.contentId = page.getContentId();
		this.albumId = page.getAlbumId();
		this.infoText = page.getInfoText();
	}

}
