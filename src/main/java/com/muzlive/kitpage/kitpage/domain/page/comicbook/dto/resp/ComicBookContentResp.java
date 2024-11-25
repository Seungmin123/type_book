package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComicBookContentResp {

	private String contentId;

	private Long contentImageUid;

	private String contentInfoText;

	private String contentTitle;

	private String category;

	private Integer totalVolume;

	private List<ComicBookResp> comicBookResps;

	public ComicBookContentResp(Content content) {
		this.contentId = content.getContentId();
		this.contentImageUid = content.getCoverImageUid();
		this.contentTitle = content.getTitle();
		this.contentInfoText = content.getInfoText();
		this.category = content.getContentType().getName();
		this.totalVolume = 0;
	}

}
