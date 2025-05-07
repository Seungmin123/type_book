package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookContentResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookContentResp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(
	description = "컨텐츠 리스트 응답 공통 클래스",
	subTypes = {ComicBookContentResp.class, PhotoBookContentResp.class},
	discriminatorProperty = "type"
)
@Getter
@Setter
public class CommonContentResp {

	private String contentId;

	private Long contentImageUid;

	private String contentInfoText;

	private String contentTitle;

	private String category;

	private Integer totalVolume;

	private String contentType;

	// Builder 로 전환 고려
	public CommonContentResp(Content content) {
		this.contentId = content.getContentId();
		this.contentImageUid = content.getCoverImageUid();
		this.contentTitle = content.getTitle();
		this.contentInfoText = content.getInfoText();
		this.category = content.getContentType().getName();
		this.totalVolume = 0;
		this.contentType = content.getContentType().getCode();
	}
}
