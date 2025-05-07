package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp.ComicBookDetailResp;
import com.muzlive.kitpage.kitpage.domain.page.photobook.dto.resp.PhotoBookDetailResp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(
	description = "컨텐츠 상세 응답 공통 클래스",
	subTypes = {ComicBookDetailResp.class, PhotoBookDetailResp.class},
	discriminatorProperty = "type"
)
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
