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

	private String category;

	private Long totalSize;

	private Integer totalVolume;

	private List<ComicBookResp> comicBookResps;

	private ComicBookResp taggedComicBook;

	public ComicBookContentResp(Content content) {
		this.contentId = content.getContentId();
		this.contentImageUid = content.getCoverImageUid();
		this.contentInfoText = content.getInfoText();
		this.category = content.getContentType().getName();
		this.totalSize = 0L;
		this.totalVolume = 0;
	}

}
