package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonContentResp {

	private String contentId;

	private Long contentImageUid;

	private String contentInfoText;

	private String contentTitle;

	private String category;

	private Integer totalVolume;

	// Builder 로 전환 고려
	public CommonContentResp(Content content) {
		this.contentId = content.getContentId();
		this.contentImageUid = content.getCoverImageUid();
		this.contentTitle = content.getTitle();
		this.contentInfoText = content.getInfoText();
		this.category = content.getContentType().getName();
		this.totalVolume = 0;
	}
}
